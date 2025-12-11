package com.barbearia;

import com.barbearia.util.DatabaseInitializer;
import com.barbearia.util.LogUtils; // Importante
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("========================================");
            System.out.println("🚀 Iniciando Barbearia Style v1.0.0");
            System.out.println("👨‍💻 Desenvolvedor: Matheus Santos & Ludson Araujo");
            System.out.println("========================================");

            // 1. Inicializar Banco de Dados (CORRIGIDO: chama init() e não initialize())
            System.out.println("🔄 Inicializando banco de dados...");
            try {
                DatabaseInitializer.init();
                System.out.println("✅ Banco de dados inicializado com sucesso!");
            } catch (Exception e) {
                LogUtils.gravarErro("Falha na inicialização do Banco (Main)", e);
                throw e; // Repassa o erro para parar o programa
            }

            // 2. Carregar interface gráfica
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/barbearia/view/fxml/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // 3. Tenta carregar CSS
            try {
                String css = Objects.requireNonNull(getClass().getResource("/com/barbearia/view/css/style.css")).toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("⚠️ Aviso: Arquivo CSS não encontrado ou com erro.");
            }

            stage.setTitle("Barbearia Style - Sistema de Gerenciamento");
            stage.setScene(scene);
            stage.setResizable(true);

            // 4. Tenta carregar ícone
            try {
                stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/com/barbearia/view/images/barber-icon.png"))));
            } catch (Exception e) {
                System.err.println("⚠️ Aviso: Ícone da janela não encontrado. Continuando sem ícone.");
            }

            stage.show();

        } catch (Exception e) {
            // Grava o erro fatal no arquivo para você me enviar
            LogUtils.gravarErro("ERRO FATAL NO MAIN.START", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            LogUtils.gravarErro("ERRO FATAL NO MAIN.MAIN", e);
            e.printStackTrace();
        }
    }
}