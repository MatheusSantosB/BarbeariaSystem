package com.barbearia.controller;

import com.barbearia.model.entity.Servico;
import com.barbearia.model.service.ServicoService;
import com.barbearia.util.Validacao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ServicoController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtPreco;
    @FXML private TextField txtDuracao;
    @FXML private TextField txtBusca;

    @FXML private TableView<Servico> tabelaServicos;
    @FXML private TableColumn<Servico, Integer> colId;
    @FXML private TableColumn<Servico, String> colNome;
    @FXML private TableColumn<Servico, String> colDescricao;
    @FXML private TableColumn<Servico, Double> colPreco;
    @FXML private TableColumn<Servico, Integer> colDuracao;

    @FXML private Label lblTotalServicos;
    @FXML private Label lblStatus;
    @FXML private Label lblValorMedio;

    private ServicoService servicoService;
    private ObservableList<Servico> servicosObservable;
    private Servico servicoSelecionado;
    private DecimalFormat decimalFormat;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoService = new ServicoService();
        servicosObservable = FXCollections.observableArrayList();
        servicoSelecionado = null;
        decimalFormat = new DecimalFormat("#,##0.00");

        configurarTabela();
        configurarFormatos();
        carregarServicos();
        atualizarStatus();
        atualizarEstatisticas();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracaoMinutos"));

        // Formatar coluna de preço
        colPreco.setCellFactory(col -> new TableCell<Servico, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("R$ " + decimalFormat.format(item));
                }
            }
        });

        // Formatar coluna de duração
        colDuracao.setCellFactory(col -> new TableCell<Servico, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + " min");
                }
            }
        });

        tabelaServicos.setItems(servicosObservable);

        // Seleção na tabela
        tabelaServicos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    servicoSelecionado = newValue;
                    if (newValue != null) {
                        preencherFormulario(newValue);
                    }
                }
        );
    }

    private void configurarFormatos() {
        // Máscara para preço
        txtPreco.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                txtPreco.setText(oldValue);
            }
        });

        // Máscara para duração (apenas números)
        txtDuracao.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDuracao.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleSalvar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            Servico servico;
            if (servicoSelecionado != null) {
                // Atualizar
                servico = servicoSelecionado;
                servico.setNome(txtNome.getText().trim());
                servico.setDescricao(txtDescricao.getText().trim());
                servico.setPreco(Double.parseDouble(txtPreco.getText()));
                servico.setDuracaoMinutos(Integer.parseInt(txtDuracao.getText()));

                servicoService.atualizarServico(servico);
                mostrarSucesso("Serviço atualizado com sucesso!");
            } else {
                // Novo
                servico = new Servico(
                        txtNome.getText().trim(),
                        txtDescricao.getText().trim(),
                        Double.parseDouble(txtPreco.getText()),
                        Integer.parseInt(txtDuracao.getText())
                );

                servicoService.cadastrarServico(servico);
                mostrarSucesso("Serviço cadastrado com sucesso!");
            }

            limparFormulario();
            carregarServicos();
            atualizarStatus();
            atualizarEstatisticas();

        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de validação", e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro ao salvar serviço", e.getMessage());
        }
    }

    @FXML
    private void handleNovo() {
        limparFormulario();
        servicoSelecionado = null;
        tabelaServicos.getSelectionModel().clearSelection();
        txtNome.requestFocus();
    }

    @FXML
    private void handleExcluir() {
        if (servicoSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um serviço para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir Serviço");
        confirmacao.setContentText("Tem certeza que deseja excluir o serviço \"" +
                servicoSelecionado.getNome() + "\"?");

        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                servicoService.excluirServico(servicoSelecionado.getId());
                mostrarSucesso("Serviço excluído com sucesso!");
                limparFormulario();
                carregarServicos();
                atualizarStatus();
                atualizarEstatisticas();
            } catch (Exception e) {
                mostrarErro("Erro ao excluir serviço", e.getMessage());
            }
        }
    }

    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            carregarServicos();
        } else {
            List<Servico> resultados = servicoService.buscarPorNome(termo);
            servicosObservable.setAll(resultados);
            atualizarStatusBusca(resultados.size(), termo);
        }
    }

    @FXML
    private void handleBuscarPorPreco() {
        try {
            TextInputDialog dialog = new TextInputDialog("50.00");
            dialog.setTitle("Buscar por Preço Máximo");
            dialog.setHeaderText("Digite o preço máximo");
            dialog.setContentText("Preço máximo (R$):");

            dialog.showAndWait().ifPresent(precoStr -> {
                try {
                    double precoMaximo = Double.parseDouble(precoStr);
                    List<Servico> resultados = servicoService.buscarPorPrecoMaximo(precoMaximo);
                    servicosObservable.setAll(resultados);
                    lblStatus.setText("Mostrando " + resultados.size() +
                            " serviço(s) até R$ " + decimalFormat.format(precoMaximo));
                } catch (NumberFormatException e) {
                    mostrarErro("Valor inválido", "Digite um valor numérico válido.");
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro na busca", e.getMessage());
        }
    }

    @FXML
    private void handleMaisPopulares() {
        try {
            TextInputDialog dialog = new TextInputDialog("5");
            dialog.setTitle("Serviços Mais Populares");
            dialog.setHeaderText("Quantos serviços deseja ver?");
            dialog.setContentText("Quantidade:");

            dialog.showAndWait().ifPresent(quantidadeStr -> {
                try {
                    int quantidade = Integer.parseInt(quantidadeStr);
                    List<Servico> resultados = servicoService.buscarMaisPopulares(quantidade);
                    servicosObservable.setAll(resultados);
                    lblStatus.setText("Top " + resultados.size() + " serviços mais populares");
                } catch (NumberFormatException e) {
                    mostrarErro("Valor inválido", "Digite um número válido.");
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro na busca", e.getMessage());
        }
    }

    @FXML
    private void handleLimparBusca() {
        txtBusca.clear();
        carregarServicos();
        atualizarStatus();
    }

    @FXML
    private void handleVoltar() {
        // Fecha a janela atual
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRelatorioFaturamento() {
        try {
            double faturamentoEstimado = servicoService.calcularFaturamentoEstimado();
            Servico maisCaro = servicoService.buscarServicoMaisCaro();
            Servico maisBarato = servicoService.buscarServicoMaisBarato();

            StringBuilder relatorio = new StringBuilder();
            relatorio.append("📊 RELATÓRIO DE SERVIÇOS\n\n");
            relatorio.append("Total de serviços: ").append(servicosObservable.size()).append("\n");
            relatorio.append("Faturamento estimado mensal: R$ ").append(decimalFormat.format(faturamentoEstimado)).append("\n");
            relatorio.append("Valor médio por serviço: R$ ").append(decimalFormat.format(servicoService.calcularValorMedioServicos())).append("\n");

            if (maisCaro != null) {
                relatorio.append("\n💰 Serviço mais caro: ").append(maisCaro.getNome())
                        .append(" - R$ ").append(decimalFormat.format(maisCaro.getPreco()));
            }

            if (maisBarato != null) {
                relatorio.append("\n💸 Serviço mais barato: ").append(maisBarato.getNome())
                        .append(" - R$ ").append(decimalFormat.format(maisBarato.getPreco()));
            }

            TextArea textArea = new TextArea(relatorio.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(400, 300);

            Alert relatorioAlert = new Alert(Alert.AlertType.INFORMATION);
            relatorioAlert.setTitle("Relatório de Serviços");
            relatorioAlert.setHeaderText("Estatísticas dos Serviços");
            relatorioAlert.getDialogPane().setContent(scrollPane);
            relatorioAlert.showAndWait();

        } catch (Exception e) {
            mostrarErro("Erro no relatório", e.getMessage());
        }
    }

    private boolean validarFormulario() {
        // Nome
        if (!Validacao.isStringValida(txtNome.getText())) {
            mostrarErro("Validação", "Nome do serviço é obrigatório.");
            txtNome.requestFocus();
            return false;
        }

        // Preço
        if (!Validacao.isStringValida(txtPreco.getText())) {
            mostrarErro("Validação", "Preço é obrigatório.");
            txtPreco.requestFocus();
            return false;
        }

        try {
            double preco = Double.parseDouble(txtPreco.getText());
            if (preco <= 0) {
                mostrarErro("Validação", "Preço deve ser maior que zero.");
                txtPreco.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Validação", "Preço inválido. Use números (ex: 35.00)");
            txtPreco.requestFocus();
            return false;
        }

        // Duração
        if (!Validacao.isStringValida(txtDuracao.getText())) {
            mostrarErro("Validação", "Duração é obrigatória.");
            txtDuracao.requestFocus();
            return false;
        }

        try {
            int duracao = Integer.parseInt(txtDuracao.getText());
            if (duracao <= 0) {
                mostrarErro("Validação", "Duração deve ser maior que zero.");
                txtDuracao.requestFocus();
                return false;
            }
            if (duracao > 240) {
                mostrarErro("Validação", "Duração não pode exceder 240 minutos (4 horas).");
                txtDuracao.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Validação", "Duração inválida. Use números inteiros (ex: 30)");
            txtDuracao.requestFocus();
            return false;
        }

        return true;
    }

    private void carregarServicos() {
        List<Servico> servicos = servicoService.listarTodos();
        servicosObservable.setAll(servicos);
    }

    private void preencherFormulario(Servico servico) {
        txtNome.setText(servico.getNome());
        txtDescricao.setText(servico.getDescricao());
        txtPreco.setText(decimalFormat.format(servico.getPreco()));
        txtDuracao.setText(String.valueOf(servico.getDuracaoMinutos()));
    }

    private void limparFormulario() {
        txtNome.clear();
        txtDescricao.clear();
        txtPreco.clear();
        txtDuracao.clear();
        servicoSelecionado = null;
    }

    private void atualizarStatus() {
        int total = servicosObservable.size();
        lblTotalServicos.setText(String.valueOf(total));
        lblStatus.setText("Total de serviços: " + total);
    }

    private void atualizarEstatisticas() {
        double valorMedio = servicoService.calcularValorMedioServicos();
        lblValorMedio.setText("Valor médio: R$ " + decimalFormat.format(valorMedio));
    }

    private void atualizarStatusBusca(int resultados, String termo) {
        lblStatus.setText("Encontrados " + resultados + " serviço(s) para: \"" + termo + "\"");
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