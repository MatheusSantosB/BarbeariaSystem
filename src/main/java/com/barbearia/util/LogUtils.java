package com.barbearia.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {

    private static final String LOG_FILE = "erros_sistema.log";

    public static void gravarErro(String contexto, Throwable erro) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("========================================");
            pw.println("DATA: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            pw.println("CONTEXTO: " + contexto);
            pw.println("MENSAGEM: " + erro.getMessage());
            pw.println("CAUSA: " + (erro.getCause() != null ? erro.getCause().getMessage() : "N/A"));
            pw.println("----------------------------------------");
            pw.println("STACK TRACE:");
            erro.printStackTrace(pw);
            pw.println("========================================\n");

            System.err.println("!!! ERRO GRAVADO NO ARQUIVO " + LOG_FILE + " !!!");

        } catch (IOException e) {
            System.err.println("Falha critica: Não foi possível gravar o log de erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}