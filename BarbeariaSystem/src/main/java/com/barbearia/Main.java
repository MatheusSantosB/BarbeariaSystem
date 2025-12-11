package com.barbearia;

import com.barbearia.util.DatabaseInitializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializar banco de dados
            inicializarBanco();

            // Carregar a tela principal
            Parent root = FXMLLoader.load(getClass().getResource("/com/barbearia/view/fxml/MainView.fxml"));

            // Configurar a cena
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/com/barbearia/view/css/styles.css").toExternalForm());

            // Configurar o palco (janela)
            primaryStage.setTitle("Barbearia Style - Sistema de Gerenciamento");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/barbearia/view/images/barber-icon.png")));
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // Configurar comportamento ao fechar
            primaryStage.setOnCloseRequest(event -> {
                event.consume(); // Consumir o evento para controlar o fechamento
                confirmarSaida(primaryStage);
            });

            // Mostrar a janela
            primaryStage.show();

            // Log de sucesso
            System.out.println("✅ Sistema iniciado com sucesso!");
            System.out.println("📁 Banco de dados: ./database/barbearia.mv.db");
            System.out.println("🌐 Interface: http://localhost:8080 (se configurado)");

        } catch (IOException e) {
            mostrarErroFatal("Erro ao carregar interface", e);
        } catch (Exception e) {
            mostrarErroFatal("Erro inesperado", e);
        }
    }

    private void inicializarBanco() {
        try {
            System.out.println("🔄 Inicializando banco de dados...");
            DatabaseInitializer.init();
            System.out.println("✅ Banco de dados inicializado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar banco de dados: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Inicialização");
            alert.setHeaderText("Falha ao inicializar banco de dados");
            alert.setContentText("O sistema não pode ser iniciado. Detalhes:\n" + e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    private void confirmarSaida(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Deseja realmente sair do sistema?");
        alert.setContentText("Todas as alterações não salvas serão perdidas.");

        // Configurar botões personalizados
        ButtonType btnSim = new ButtonType("Sim, Sair");
        ButtonType btnNao = new ButtonType("Não, Continuar");
        ButtonType btnMinimizar = new ButtonType("Minimizar");

        alert.getButtonTypes().setAll(btnSim, btnNao, btnMinimizar);

        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent()) {
            if (resultado.get() == btnSim) {
                // Salvar configurações antes de sair (se necessário)
                salvarConfiguracoes();
                Platform.exit();
            } else if (resultado.get() == btnMinimizar) {
                stage.setIconified(true); // Minimizar
            }
            // Se escolher "Não", apenas continua
        }
    }

    private void salvarConfiguracoes() {
        try {
            System.out.println("💾 Salvando configurações...");
            // Aqui você pode adicionar lógica para salvar configurações
            System.out.println("✅ Configurações salvas com sucesso!");
        } catch (Exception e) {
            System.err.println("⚠️ Erro ao salvar configurações: " + e.getMessage());
        }
    }

    private void mostrarErroFatal(String titulo, Exception e) {
        System.err.println("❌ " + titulo + ": " + e.getMessage());
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro Fatal");
        alert.setHeaderText(titulo);
        alert.setContentText("O sistema encontrou um erro crítico:\n\n"
                + e.getMessage()
                + "\n\nO sistema será fechado.");
        alert.showAndWait();

        Platform.exit();
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🚀 Iniciando Barbearia Style v1.0.0");
        System.out.println("👨‍💻 Desenvolvedor: Matheus Santos");
        System.out.println("🎓 Disciplina: POO - UFRN");
        System.out.println("👨‍🏫 Professor: Josenalde Oliveira");
        System.out.println("========================================");

        // Verificar Java version
        String javaVersion = System.getProperty("java.version");
        System.out.println("☕ Java Version: " + javaVersion);

        // Verificar sistema operacional
        String os = System.getProperty("os.name");
        System.out.println("💻 Sistema Operacional: " + os);

        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("💥 Erro crítico ao iniciar aplicação: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    // Métodos utilitários estáticos
    public static void mostrarMensagemInfo(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }

    public static void mostrarMensagemErro(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }

    public static void mostrarMensagemAlerta(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }
}