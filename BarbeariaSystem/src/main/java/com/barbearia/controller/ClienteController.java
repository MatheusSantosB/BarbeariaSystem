package com.barbearia.controller;

import com.barbearia.model.entity.Cliente;
import com.barbearia.model.service.ClienteService;
import com.barbearia.util.DateUtils;
import com.barbearia.util.Validacao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dtDataCadastro;
    @FXML private TextField txtBusca;

    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colTelefone;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colDataCadastro;

    @FXML private Label lblTotalClientes;
    @FXML private Label lblStatus;

    private ClienteService clienteService;
    private ObservableList<Cliente> clientesObservable;
    private Cliente clienteSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteService = new ClienteService();
        clientesObservable = FXCollections.observableArrayList();
        clienteSelecionado = null;

        configurarTabela();
        configurarDatePicker();
        carregarClientes();
        atualizarStatus();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDataCadastro.setCellValueFactory(cellData -> {
            LocalDate data = cellData.getValue().getDataCadastro();
            return new javafx.beans.property.SimpleStringProperty(
                    DateUtils.formatarData(data)
            );
        });

        tabelaClientes.setItems(clientesObservable);

        // Seleção na tabela
        tabelaClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    clienteSelecionado = newValue;
                    if (newValue != null) {
                        preencherFormulario(newValue);
                    }
                }
        );
    }

    private void configurarDatePicker() {
        dtDataCadastro.setValue(LocalDate.now());
        dtDataCadastro.setEditable(false);
    }

    @FXML
    private void handleSalvar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            Cliente cliente;
            if (clienteSelecionado != null) {
                // Atualizar
                cliente = clienteSelecionado;
                cliente.setNome(txtNome.getText().trim());
                cliente.setTelefone(txtTelefone.getText().trim());
                cliente.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());

                clienteService.atualizarCliente(cliente);
                mostrarSucesso("Cliente atualizado com sucesso!");
            } else {
                // Novo
                cliente = new Cliente(
                        txtNome.getText().trim(),
                        txtTelefone.getText().trim(),
                        txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim()
                );

                cliente.setDataCadastro(dtDataCadastro.getValue());

                clienteService.cadastrarCliente(cliente);
                mostrarSucesso("Cliente cadastrado com sucesso!");
            }

            limparFormulario();
            carregarClientes();
            atualizarStatus();

        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de validação", e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro ao salvar cliente", e.getMessage());
        }
    }

    @FXML
    private void handleNovo() {
        limparFormulario();
        clienteSelecionado = null;
        tabelaClientes.getSelectionModel().clearSelection();
        txtNome.requestFocus();
    }

    @FXML
    private void handleExcluir() {
        if (clienteSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um cliente para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir Cliente");
        confirmacao.setContentText("Tem certeza que deseja excluir o cliente " +
                clienteSelecionado.getNome() + "?");

        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                clienteService.excluirCliente(clienteSelecionado.getId());
                mostrarSucesso("Cliente excluído com sucesso!");
                limparFormulario();
                carregarClientes();
                atualizarStatus();
            } catch (Exception e) {
                mostrarErro("Erro ao excluir cliente", e.getMessage());
            }
        }
    }

    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            carregarClientes();
        } else {
            List<Cliente> resultados = clienteService.buscarPorNome(termo);
            clientesObservable.setAll(resultados);
            atualizarStatusBusca(resultados.size(), termo);
        }
    }

    @FXML
    private void handleLimparBusca() {
        txtBusca.clear();
        carregarClientes();
        atualizarStatus();
    }

    @FXML
    private void handleExportar() {
        // Implementação simplificada
        mostrarInformacao("Exportar", "Funcionalidade de exportação em desenvolvimento.");
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

        // Email (opcional, mas se preenchido, deve ser válido)
        if (!txtEmail.getText().trim().isEmpty() &&
                !Validacao.isEmailValido(txtEmail.getText().trim())) {
            mostrarErro("Validação", "Email inválido.");
            txtEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void carregarClientes() {
        List<Cliente> clientes = clienteService.listarTodos();
        clientesObservable.setAll(clientes);
    }

    private void preencherFormulario(Cliente cliente) {
        txtNome.setText(cliente.getNome());
        txtTelefone.setText(cliente.getTelefone());
        txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
        dtDataCadastro.setValue(cliente.getDataCadastro());
    }

    private void limparFormulario() {
        txtNome.clear();
        txtTelefone.clear();
        txtEmail.clear();
        dtDataCadastro.setValue(LocalDate.now());
        clienteSelecionado = null;
    }

    private void atualizarStatus() {
        int total = clientesObservable.size();
        lblTotalClientes.setText(String.valueOf(total));
        lblStatus.setText("Total de clientes: " + total);
    }

    private void atualizarStatusBusca(int resultados, String termo) {
        lblStatus.setText("Encontrados " + resultados + " cliente(s) para: \"" + termo + "\"");
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

    private void mostrarInformacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}