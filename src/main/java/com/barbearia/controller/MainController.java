package com.barbearia.controller;

import com.barbearia.util.LogUtils; // Importante
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML
    private void handleClientes() {
        try {
            carregarTela("/com/barbearia/view/fxml/ClienteView.fxml", "Gerenciamento de Clientes");
        } catch (Exception e) {
            LogUtils.gravarErro("Erro ao abrir tela de Clientes", e);
            mostrarErro("Erro ao carregar tela de clientes. Veja o log para detalhes.");
        }
    }

    @FXML
    private void handleProfissionais() {
        try {
            carregarTela("/com/barbearia/view/fxml/ProfissionalView.fxml", "Gerenciamento de Profissionais");
        } catch (Exception e) {
            LogUtils.gravarErro("Erro ao abrir tela de Profissionais", e);
            mostrarErro("Erro ao carregar tela de profissionais. Veja o log para detalhes.");
        }
    }

    @FXML
    private void handleServicos() {
        try {
            carregarTela("/com/barbearia/view/fxml/ServicoView.fxml", "Gerenciamento de Serviços");
        } catch (Exception e) {
            LogUtils.gravarErro("Erro ao abrir tela de Serviços", e);
            mostrarErro("Erro ao carregar tela de serviços. Veja o log para detalhes.");
        }
    }

    @FXML
    private void handleAgendamentos() {
        try {
            carregarTela("/com/barbearia/view/fxml/AgendamentoView.fxml", "Gerenciamento de Agendamentos");
        } catch (Exception e) {
            LogUtils.gravarErro("Erro ao abrir tela de Agendamentos", e);
            mostrarErro("Erro ao carregar tela de agendamentos. Veja o log para detalhes.");
        }
    }

    @FXML
    private void handleRelatorios() {
        mostrarInformacao("Funcionalidade em desenvolvimento",
                "A tela de relatórios estará disponível em breve!");
    }

    @FXML
    private void handleSobre() {
        Alert sobre = new Alert(Alert.AlertType.INFORMATION);
        sobre.setTitle("Sobre o Sistema");
        sobre.setHeaderText("Barbearia Style - Sistema de Gerenciamento");
        sobre.setContentText("""
            Versão: 1.0.0
            Desenvolvido por: Matheus Santos
            Disciplina: Programação Orientada a Objetos
            Professor: Josenalde Oliveira
            UFRN - Escola Agrícola de Jundiaí
            
            Sistema desenvolvido em JavaFX com arquitetura MVC.
            Banco de dados: H2 Database
            """);
        sobre.showAndWait();
    }

    @FXML
    private void handleSair() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de Saída");
        confirmacao.setHeaderText("Deseja realmente sair do sistema?");
        confirmacao.setContentText("Todas as alterações não salvas serão perdidas.");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void carregarTela(String fxmlPath, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Barbearia Style - " + titulo);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
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