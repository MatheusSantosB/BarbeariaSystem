package com.barbearia.controller;

import com.barbearia.model.entity.*;
import com.barbearia.model.service.AgendamentoService;
import com.barbearia.model.service.ClienteService;
import com.barbearia.model.service.ProfissionalService;
import com.barbearia.model.service.ServicoService;
import com.barbearia.util.DateUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List; // Importação que estava faltando
import java.util.ResourceBundle;

public class AgendamentoController implements Initializable {

    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Profissional> cbProfissional;
    @FXML private DatePicker dtData;
    @FXML private ComboBox<LocalTime> cbHora;
    @FXML private ListView<Servico> listServicos;
    @FXML private TextArea txtObservacoes;
    @FXML private TextField txtBusca;

    @FXML private TableView<Agendamento> tabelaAgendamentos;
    @FXML private TableColumn<Agendamento, Integer> colId;
    @FXML private TableColumn<Agendamento, String> colCliente;
    @FXML private TableColumn<Agendamento, String> colProfissional;
    @FXML private TableColumn<Agendamento, String> colData;
    @FXML private TableColumn<Agendamento, String> colHora;
    @FXML private TableColumn<Agendamento, String> colStatus;
    @FXML private TableColumn<Agendamento, Double> colValor;

    @FXML private Label lblTotalAgendamentos;
    @FXML private Label lblStatus;
    @FXML private Label lblValorTotal;
    @FXML private Label lblDuracaoTotal;

    private AgendamentoService agendamentoService;
    private ClienteService clienteService;
    private ProfissionalService profissionalService;
    private ServicoService servicoService;

    private ObservableList<Agendamento> agendamentosObservable;
    private ObservableList<Cliente> clientesObservable;
    private ObservableList<Profissional> profissionaisObservable;
    private ObservableList<Servico> servicosObservable;
    private ObservableList<LocalTime> horariosObservable;

    private Agendamento agendamentoSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        agendamentoService = new AgendamentoService();
        clienteService = new ClienteService();
        profissionalService = new ProfissionalService();
        servicoService = new ServicoService();

        agendamentosObservable = FXCollections.observableArrayList();
        clientesObservable = FXCollections.observableArrayList();
        profissionaisObservable = FXCollections.observableArrayList();
        servicosObservable = FXCollections.observableArrayList();
        horariosObservable = FXCollections.observableArrayList();

        agendamentoSelecionado = null;

        configurarTabela();
        configurarCombos();
        configurarDatePicker();
        configurarListaServicos();
        configurarHorarios();

        carregarDados();
        atualizarStatus();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colCliente.setCellValueFactory(cellData -> {
            Cliente cliente = cellData.getValue().getCliente();
            return new SimpleStringProperty(cliente != null ? cliente.getNome() : "");
        });

        colProfissional.setCellValueFactory(cellData -> {
            Profissional profissional = cellData.getValue().getProfissional();
            return new SimpleStringProperty(profissional != null ? profissional.getNome() : "");
        });

        colData.setCellValueFactory(cellData -> {
            LocalDate data = cellData.getValue().getData();
            return new SimpleStringProperty(DateUtils.formatarData(data));
        });

        colHora.setCellValueFactory(cellData -> {
            LocalTime hora = cellData.getValue().getHora();
            return new SimpleStringProperty(DateUtils.formatarHora(hora));
        });

        // Conversão segura de Enum para String
        colStatus.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStatus() != null) {
                return new SimpleStringProperty(cellData.getValue().getStatus().toString());
            }
            return new SimpleStringProperty("");
        });

        colValor.setCellValueFactory(cellData -> {
            Double valor = cellData.getValue().calcularValorTotal();
            return new SimpleObjectProperty<>(valor);
        });

        colValor.setCellFactory(col -> new TableCell<Agendamento, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("R$ %.2f", item));
                }
            }
        });

        colStatus.setCellFactory(col -> new TableCell<Agendamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "AGENDADO":
                            setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            break;
                        case "CONFIRMADO":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "REALIZADO":
                            setStyle("-fx-text-fill: darkgreen;");
                            break;
                        case "CANCELADO":
                            setStyle("-fx-text-fill: red;");
                            break;
                        case "AUSENTE":
                            setStyle("-fx-text-fill: orange;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        tabelaAgendamentos.setItems(agendamentosObservable);

        tabelaAgendamentos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    agendamentoSelecionado = newValue;
                    if (newValue != null) {
                        preencherFormulario(newValue);
                    }
                }
        );
    }

    private void configurarCombos() {
        cbCliente.setItems(clientesObservable);
        cbCliente.setCellFactory(param -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { // Verificação crucial contra NPE
                    setText(null);
                } else {
                    setText(item.getNome() + " - " + item.getTelefone());
                }
            }
        });
        cbCliente.setButtonCell(new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { // Verificação crucial contra NPE
                    setText(null);
                } else {
                    setText(item.getNome());
                }
            }
        });

        cbProfissional.setItems(profissionaisObservable);
        cbProfissional.setCellFactory(param -> new ListCell<Profissional>() {
            @Override
            protected void updateItem(Profissional item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { // Verificação crucial contra NPE
                    setText(null);
                } else {
                    setText(item.getNome() + " - " + item.getEspecialidade());
                }
            }
        });
        cbProfissional.setButtonCell(new ListCell<Profissional>() {
            @Override
            protected void updateItem(Profissional item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { // Verificação crucial contra NPE
                    setText(null);
                } else {
                    setText(item.getNome());
                }
            }
        });
    }

    private void configurarDatePicker() {
        dtData.setValue(LocalDate.now());
        dtData.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    private void configurarListaServicos() {
        listServicos.setItems(servicosObservable);
        listServicos.setCellFactory(param -> new ListCell<Servico>() {
            @Override
            protected void updateItem(Servico item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - R$ %.2f (%d min)",
                            item.getNome(), item.getPreco(), item.getDuracaoMinutos()));
                }
            }
        });

        listServicos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void configurarHorarios() {
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(20, 0);

        LocalTime horario = inicio;
        while (!horario.isAfter(fim)) {
            horariosObservable.add(horario);
            horario = horario.plusMinutes(30);
        }

        cbHora.setItems(horariosObservable);
        cbHora.setCellFactory(param -> new ListCell<LocalTime>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DateUtils.formatarHora(item));
                }
            }
        });

        LocalTime agora = LocalTime.now();
        // Correção de 'agro' para 'agora'
        LocalTime proximoHorario = horariosObservable.stream()
                .filter(h -> h.isAfter(agora) || h.equals(agora))
                .findFirst()
                .orElse(horariosObservable.get(0));
        cbHora.setValue(proximoHorario);
    }

    @FXML
    private void handleSalvar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            if (agendamentoSelecionado != null) {
                atualizarAgendamentoExistente();
                mostrarSucesso("Agendamento atualizado com sucesso!");
            } else {
                criarNovoAgendamento();
                mostrarSucesso("Agendamento realizado com sucesso!");
            }

            limparFormulario();
            carregarAgendamentos();
            atualizarStatus();

        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de validação", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar agendamento", e.getMessage());
        }
    }

    private void atualizarAgendamentoExistente() {
        agendamentoSelecionado.setCliente(cbCliente.getValue());
        agendamentoSelecionado.setProfissional(cbProfissional.getValue());
        agendamentoSelecionado.setData(dtData.getValue());
        agendamentoSelecionado.setHora(cbHora.getValue());
        agendamentoSelecionado.setObservacoes(txtObservacoes.getText().trim());

        agendamentoSelecionado.getServicos().clear();
        agendamentoSelecionado.getServicos().addAll(
                listServicos.getSelectionModel().getSelectedItems()
        );

        agendamentoService.atualizarAgendamento(agendamentoSelecionado);
    }

    private void criarNovoAgendamento() {
        Agendamento agendamento = new Agendamento(
                dtData.getValue(),
                cbHora.getValue(),
                cbCliente.getValue(),
                cbProfissional.getValue()
        );

        agendamento.setObservacoes(txtObservacoes.getText().trim());
        agendamento.getServicos().addAll(
                listServicos.getSelectionModel().getSelectedItems()
        );

        agendamentoService.agendar(agendamento);
    }

    @FXML
    private void handleNovo() {
        limparFormulario();
        agendamentoSelecionado = null;
        tabelaAgendamentos.getSelectionModel().clearSelection();
        cbCliente.requestFocus();
    }

    @FXML
    private void handleCancelar() {
        if (agendamentoSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um agendamento para cancelar.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Agendamento");
        dialog.setHeaderText("Cancelamento do agendamento #" + agendamentoSelecionado.getId());
        dialog.setContentText("Motivo do cancelamento:");

        dialog.showAndWait().ifPresent(motivo -> {
            if (!motivo.trim().isEmpty()) {
                agendamentoService.cancelarAgendamento(agendamentoSelecionado.getId(), motivo);
                mostrarSucesso("Agendamento cancelado com sucesso!");
                carregarAgendamentos();
                atualizarStatus();
            }
        });
    }

    @FXML
    private void handleConfirmar() {
        if (agendamentoSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um agendamento para confirmar.");
            return;
        }

        agendamentoService.confirmarAgendamento(agendamentoSelecionado.getId());
        mostrarSucesso("Agendamento confirmado com sucesso!");
        carregarAgendamentos();
        atualizarStatus();
    }

    @FXML
    private void handleFinalizar() {
        if (agendamentoSelecionado == null) {
            mostrarAviso("Seleção necessária", "Selecione um agendamento para finalizar.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Finalizar Agendamento");
        dialog.setHeaderText("Finalização do agendamento #" + agendamentoSelecionado.getId());
        dialog.setContentText("Observações finais:");

        dialog.showAndWait().ifPresent(observacoes -> {
            agendamentoService.finalizarAgendamento(agendamentoSelecionado.getId(), observacoes);
            mostrarSucesso("Agendamento finalizado com sucesso!");
            carregarAgendamentos();
            atualizarStatus();
        });
    }

    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            carregarAgendamentos();
        } else {
            List<Agendamento> resultados = agendamentoService.listarTodos().stream()
                    .filter(a -> a.getCliente().getNome().toLowerCase().contains(termo.toLowerCase()))
                    .toList();
            agendamentosObservable.setAll(resultados);
            atualizarStatusBusca(resultados.size(), termo);
        }
    }

    @FXML
    private void handleBuscarHoje() {
        List<Agendamento> agendamentosHoje = agendamentoService.buscarAgendamentosHoje();
        agendamentosObservable.setAll(agendamentosHoje);
        lblStatus.setText("Agendamentos para hoje: " + agendamentosHoje.size());
    }

    @FXML
    private void handleBuscarPorPeriodo() {
        mostrarInformacao("Busca por período", "Funcionalidade em desenvolvimento.");
    }

    @FXML
    private void handleLimparBusca() {
        txtBusca.clear();
        carregarAgendamentos();
        atualizarStatus();
    }

    @FXML
    private void handleCalcularValor() {
        calcularValorEDuracao();
    }

    @FXML
    private void handleVoltar() {
        Stage stage = (Stage) cbCliente.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRelatorioFaturamento() {
        try {
            LocalDate inicio = LocalDate.now().minusDays(30);
            LocalDate fim = LocalDate.now();

            double faturamento = agendamentoService.calcularFaturamentoPeriodo(inicio, fim);
            int pendentes = agendamentoService.contarAgendamentosPendentes();
            double taxaCancelamento = agendamentoService.calcularTaxaCancelamento();

            StringBuilder relatorio = new StringBuilder();
            relatorio.append("📊 RELATÓRIO DE AGENDAMENTOS\n\n");
            relatorio.append("Período: ").append(DateUtils.formatarData(inicio))
                    .append(" a ").append(DateUtils.formatarData(fim)).append("\n");
            relatorio.append("Faturamento: R$ ").append(String.format("%.2f", faturamento)).append("\n");
            relatorio.append("Agendamentos pendentes: ").append(pendentes).append("\n");
            relatorio.append("Taxa de cancelamento: ").append(String.format("%.1f%%", taxaCancelamento)).append("\n");

            TextArea textArea = new TextArea(relatorio.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(400, 200);

            Alert relatorioAlert = new Alert(Alert.AlertType.INFORMATION);
            relatorioAlert.setTitle("Relatório de Agendamentos");
            relatorioAlert.setHeaderText("Estatísticas dos Últimos 30 Dias");
            relatorioAlert.getDialogPane().setContent(scrollPane);
            relatorioAlert.showAndWait();

        } catch (Exception e) {
            mostrarErro("Erro no relatório", e.getMessage());
        }
    }

    private boolean validarFormulario() {
        if (cbCliente.getValue() == null) {
            mostrarErro("Validação", "Selecione um cliente.");
            cbCliente.requestFocus();
            return false;
        }

        if (cbProfissional.getValue() == null) {
            mostrarErro("Validação", "Selecione um profissional.");
            cbProfissional.requestFocus();
            return false;
        }

        if (dtData.getValue() == null) {
            mostrarErro("Validação", "Selecione uma data.");
            dtData.requestFocus();
            return false;
        }

        if (cbHora.getValue() == null) {
            mostrarErro("Validação", "Selecione um horário.");
            cbHora.requestFocus();
            return false;
        }

        if (listServicos.getSelectionModel().getSelectedItems().isEmpty()) {
            mostrarErro("Validação", "Selecione pelo menos um serviço.");
            listServicos.requestFocus();
            return false;
        }

        return true;
    }

    private void carregarDados() {
        clientesObservable.setAll(clienteService.listarTodos());
        profissionaisObservable.setAll(profissionalService.buscarAtivos());
        servicosObservable.setAll(servicoService.listarTodos());
        carregarAgendamentos();
    }

    private void carregarAgendamentos() {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        agendamentosObservable.setAll(agendamentos);
    }

    private void preencherFormulario(Agendamento agendamento) {
        cbCliente.setValue(agendamento.getCliente());
        cbProfissional.setValue(agendamento.getProfissional());
        dtData.setValue(agendamento.getData());
        cbHora.setValue(agendamento.getHora());
        txtObservacoes.setText(agendamento.getObservacoes());

        listServicos.getSelectionModel().clearSelection();
        for (Servico servico : agendamento.getServicos()) {
            int index = servicosObservable.indexOf(servico);
            if (index >= 0) {
                listServicos.getSelectionModel().select(index);
            }
        }

        calcularValorEDuracao();
    }

    private void limparFormulario() {
        cbCliente.setValue(null);
        cbProfissional.setValue(null);
        dtData.setValue(LocalDate.now());
        configurarHorarios();
        listServicos.getSelectionModel().clearSelection();
        txtObservacoes.clear();
        agendamentoSelecionado = null;

        lblValorTotal.setText("R$ 0,00");
        lblDuracaoTotal.setText("0 min");
    }

    private void calcularValorEDuracao() {
        double valorTotal = 0.0;
        int duracaoTotal = 0;

        for (Servico servico : listServicos.getSelectionModel().getSelectedItems()) {
            valorTotal += servico.getPreco();
            duracaoTotal += servico.getDuracaoMinutos();
        }

        lblValorTotal.setText(String.format("R$ %.2f", valorTotal));
        lblDuracaoTotal.setText(duracaoTotal + " min");
    }

    private void atualizarStatus() {
        int total = agendamentosObservable.size();
        int pendentes = agendamentoService.contarAgendamentosPendentes();
        if (lblTotalAgendamentos != null) {
            lblTotalAgendamentos.setText(String.valueOf(total));
        }
        lblStatus.setText("Total: " + total + " | Pendentes: " + pendentes);
    }

    private void atualizarStatusBusca(int resultados, String termo) {
        lblStatus.setText("Encontrados " + resultados + " agendamento(s) para: \"" + termo + "\"");
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