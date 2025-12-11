package com.barbearia.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    // Formatters
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final DateTimeFormatter DATE_DB_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Formatar data
    public static String formatarData(LocalDate data) {
        if (data == null) return "";
        return data.format(DATE_FORMATTER);
    }

    // Formatar hora
    public static String formatarHora(LocalTime hora) {
        if (hora == null) return "";
        return hora.format(TIME_FORMATTER);
    }

    // Formatar data e hora
    public static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) return "";
        return dataHora.format(DATE_TIME_FORMATTER);
    }

    // Formatar para banco de dados
    public static String formatarDataParaDB(LocalDate data) {
        if (data == null) return "";
        return data.format(DATE_DB_FORMATTER);
    }

    // Converter string para data
    public static LocalDate parseData(String dataStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Tenta vários formatos
            if (dataStr.contains("/")) {
                return LocalDate.parse(dataStr, DATE_FORMATTER);
            } else if (dataStr.contains("-")) {
                return LocalDate.parse(dataStr, DATE_DB_FORMATTER);
            }
        } catch (Exception e) {
            // Ignora erros
        }

        return null;
    }

    // Converter string para hora
    public static LocalTime parseHora(String horaStr) {
        if (horaStr == null || horaStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(horaStr, TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    // Obter data atual formatada
    public static String getDataAtualFormatada() {
        return formatarData(LocalDate.now());
    }

    // Obter hora atual formatada
    public static String getHoraAtualFormatada() {
        return formatarHora(LocalTime.now());
    }

    // Calcular diferença em dias entre duas datas
    public static long calcularDiferencaDias(LocalDate data1, LocalDate data2) {
        if (data1 == null || data2 == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(data1, data2);
    }

    // Verificar se data está entre duas datas
    public static boolean isDataEntre(LocalDate data, LocalDate inicio, LocalDate fim) {
        if (data == null || inicio == null || fim == null) {
            return false;
        }
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    // Gerar lista de datas entre um período
    public static List<LocalDate> gerarListaDatas(LocalDate inicio, LocalDate fim) {
        List<LocalDate> datas = new ArrayList<>();

        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            return datas;
        }

        LocalDate data = inicio;
        while (!data.isAfter(fim)) {
            datas.add(data);
            data = data.plusDays(1);
        }

        return datas;
    }

    // Verificar se é fim de semana
    public static boolean isFimDeSemana(LocalDate data) {
        if (data == null) return false;

        java.time.DayOfWeek dia = data.getDayOfWeek();
        return dia == java.time.DayOfWeek.SATURDAY || dia == java.time.DayOfWeek.SUNDAY;
    }

    // Adicionar dias úteis (ignora fins de semana)
    public static LocalDate adicionarDiasUteis(LocalDate data, int dias) {
        if (data == null) return null;

        LocalDate result = data;
        int diasAdicionados = 0;

        while (diasAdicionados < dias) {
            result = result.plusDays(1);

            // Se não for fim de semana, conta como dia útil
            if (!isFimDeSemana(result)) {
                diasAdicionados++;
            }
        }

        return result;
    }

    // Obter primeiro dia do mês
    public static LocalDate getPrimeiroDiaMes(LocalDate data) {
        if (data == null) return null;
        return data.withDayOfMonth(1);
    }

    // Obter último dia do mês
    public static LocalDate getUltimoDiaMes(LocalDate data) {
        if (data == null) return null;
        return data.withDayOfMonth(data.lengthOfMonth());
    }

    // Calcular idade a partir da data de nascimento
    public static int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) return 0;

        LocalDate hoje = LocalDate.now();
        return (int) ChronoUnit.YEARS.between(dataNascimento, hoje);
    }

    // Verificar se é maior de idade
    public static boolean isMaiorDeIdade(LocalDate dataNascimento) {
        return calcularIdade(dataNascimento) >= 18;
    }

    // Gerar horários disponíveis para agendamento
    public static List<LocalTime> gerarHorariosDisponiveis(LocalTime inicio, LocalTime fim, int intervaloMinutos) {
        List<LocalTime> horarios = new ArrayList<>();

        if (inicio == null || fim == null || intervaloMinutos <= 0) {
            return horarios;
        }

        LocalTime horario = inicio;
        while (!horario.isAfter(fim)) {
            horarios.add(horario);
            horario = horario.plusMinutes(intervaloMinutos);
        }

        return horarios;
    }

    // Converter LocalDateTime para timestamp (milissegundos)
    public static long toTimestamp(LocalDateTime dataHora) {
        if (dataHora == null) return 0;

        return dataHora.atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    // Converter timestamp para LocalDateTime
    public static LocalDateTime fromTimestamp(long timestamp) {
        return java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}