package com.barbearia.controller;

import com.barbearia.model.entity.Profissional;
import com.barbearia.model.service.ProfissionalService;
import com.barbearia.util.Validacao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfissionalController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private TextField txtEspecialidade;
    @FXML private TextField txtTelefone;
    @FXML private CheckBox chkAtivo;
    @FXML private TextField txtBusca;

    @FXML private TableView<Profissional> tabelaProfissionais;
    @FXML private TableColumn<Profissional, Integer> colId;
    @FXML private TableColumn<Profissional, String> colNome;
    @FXML private TableColumn<Profissional, String> colEspecialidade;
    @FXML private TableColumn<Profissional, String> colTelefone;
    @FXML private TableColumn<Profissional, Boolean> colAtivo;

    @FXML private Label lblTotalProfissionais;
    @FXML private Label lblStatus;

    private ProfissionalService profissionalService;
    private ObservableList<Profissional> profissionaisObservable;
    private Profissional profissionalSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        profissionalService = new ProfissionalService();
        profissionaisObservable = FXCollections.observableArrayList();
        profissionalSelecionado = null;

        configurarTabela();
        carregarProfissionais();
        atualizarStatus();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEspecialidade.setCellValueFactory(new PropertyValueFactory<>("especialidade"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));

        tabelaProfissionais.setItems(profissionaisObservable);

        // Personalizar coluna de ativo
        colAtivo.setCellFactory(col -> new TableCell<Profissional, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Ativo" : "Inativo");
                    if (item) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });

        // Seleção na tabela
        tabelaProfissionais.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    profissionalSelecionado = newValue;
                    if (newValue != null) {
                        preencherFormulario(newValue);
                    }
                }
        );
    }

    @FXML
    private void handleSalvar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            Profissional profissional;
            if (profissionalSelecionado != null) {
                // Atualizar
                profissional = profissionalSelecionado;
                profissional.setNome(txtNome.getText().trim());
                profissional.setEspecialidade(txtEspecialidade.getText().trim());
                profissional.setTelefone(txtTelefone.getText().trim());
                profissional.setAtivo(chkAtivo.isSelected());

                profissionalService.atualizarProfissional(profissional);
                mostrarSucesso("Profissional atualizado com sucesso!");
            } else {
                // Novo
                profissional = new Profissional(
                        txtNome.getText().trim(),
                        txtEspecialidade.getText().trim(),
                        txtTelefone.getText().trim()
                );
                profissional.setAtivo(chkAtivo.isSelected());

                profissionalService.cadastrarProfissional(profissional);
                mostrarSucesso("Profissional cadastrado com sucesso!");
            }

            limparFormulario();
            carregarProfissionais();
            atualizarStatus();

        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de validação", e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro ao salvar profissional", e.getMessage());
        }
    }

    @FXML
    private void handleNovo() {
        limparFormulario();
        profissionalSelecionado = null;
        tabelaProfissionais.getSelectionModel().clearSelection();
        txtNome.requestFocus();
        chkAtivo.setSelected(true);
    }

    @FXML
    private void handleExcluir() {
        if (profissionalSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um profissional para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir Profissional");
        confirmacao.setContentText("Tem certeza que deseja excluir o profissional " +
                profissionalSelecionado.getNome() + "?");

        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                profissionalService.excluirProfissional(profissionalSelecionado.getId());
                mostrarSucesso("Profissional excluído com sucesso!");
                limparFormulario();
                carregarProfissionais();
                atualizarStatus();
            } catch (Exception e) {
                mostrarErro("Erro ao excluir profissional", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAtivar() {
        if (profissionalSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um profissional para ativar.");
            return;
        }

        profissionalSelecionado.setAtivo(true);
        profissionalService.atualizarProfissional(profissionalSelecionado);
        mostrarSucesso("Profissional ativado com sucesso!");
        carregarProfissionais();
    }

    @FXML
    private void handleDesativar() {
        if (profissionalSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um profissional para desativar.");
            return;
        }

        profissionalSelecionado.setAtivo(false);
        profissionalService.atualizarProfissional(profissionalSelecionado);
        mostrarSucesso("Profissional desativado com sucesso!");
        carregarProfissionais();
    }

    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            carregarProfissionais();
        } else {
            List<Profissional> resultados = profissionalService.buscarPorNome(termo);
            profissionaisObservable.setAll(resultados);
            atualizarStatusBusca(resultados.size(), termo);
        }
    }

    @FXML
    private void handleBuscarAtivos() {
        List<Profissional> ativos = profissionalService.buscarAtivos();
        profissionaisObservable.setAll(ativos);
        lblStatus.setText("Mostrando " + ativos.size() + " profissional(is) ativo(s)");
    }

    @FXML
    private void handleLimparBusca() {
        txtBusca.clear();
        carregarProfissionais();
        atualizarStatus();
    }

    @FXML
    private void handleVoltar() {
        // Fecha a janela atual
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        // Nome
        if (!Validacao.isStringValida(txtNome.getText())) {
            mostrarErro("Validação", "Nome é obrigatório.");
            txtNome.requestFocus();
            return false;
        }

        // Especialidade
        if (!Validacao.isStringValida(txtEspecialidade.getText())) {
            mostrarErro("Validação", "Especialidade é obrigatória.");
            txtEspecialidade.requestFocus();
            return false;
        }

        // Telefone
        if (!Validacao.isStringValida(txtTelefone.getText())) {
            mostrarErro("Validação", "Telefone é obrigatório.");
            txtTelefone.requestFocus();
            return false;
        }

        if (!Validacao.isTelefoneValido(txtTelefone.getText())) {
            mostrarErro("Validação", "Telefone inválido. Use formato (XX) XXXXX-XXXX");
            txtTelefone.requestFocus();
            return false;
        }

        return true;
    }

    private void carregarProfissionais() {
        List<Profissional> profissionais = profissionalService.listarTodos();
        profissionaisObservable.setAll(profissionais);
    }

    private void preencherFormulario(Profissional profissional) {
        txtNome.setText(profissional.getNome());
        txtEspecialidade.setText(profissional.getEspecialidade());
        txtTelefone.setText(profissional.getTelefone());
        chkAtivo.setSelected(profissional.isAtivo());
    }

    private void limparFormulario() {
        txtNome.clear();
        txtEspecialidade.clear();
        txtTelefone.clear();
        chkAtivo.setSelected(true);
        profissionalSelecionado = null;
    }

    private void atualizarStatus() {
        int total = profissionaisObservable.size();
        int ativos = profissionalService.contarProfissionaisAtivos();
        lblTotalProfissionais.setText(String.valueOf(total));
        lblStatus.setText("Total: " + total + " | Ativos: " + ativos + " | Inativos: " + (total - ativos));
    }

    private void atualizarStatusBusca(int resultados, String termo) {
        lblStatus.setText("Encontrados " + resultados + " profissional(is) para: \"" + termo + "\"");
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAviso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}