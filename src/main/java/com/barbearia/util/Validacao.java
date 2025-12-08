package com.barbearia.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

public class Validacao {

    // Padrões de validação
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern TELEFONE_PATTERN =
            Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$");

    private static final Pattern CPF_PATTERN =
            Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    private static final Pattern APENAS_LETRAS_PATTERN =
            Pattern.compile("^[a-zA-ZÀ-ÿ\\s]+$");

    private static final Pattern APENAS_NUMEROS_PATTERN =
            Pattern.compile("^[0-9]+$");

    // Validação de email
    public static boolean isEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Validação de telefone
    public static boolean isTelefoneValido(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }

        // Remove espaços, parênteses, traços
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");

        // Verifica se tem entre 10 e 11 dígitos
        return telefoneLimpo.length() >= 10 && telefoneLimpo.length() <= 11;
    }

    // Validação de CPF (formato básico)
    public static boolean isCPFValido(String cpf) {
        if (cpf == null) return false;

        // Verifica formato
        if (!CPF_PATTERN.matcher(cpf).matches()) {
            return false;
        }

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica dígitos repetidos (ex: 111.111.111-11)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validação dos dígitos verificadores
        try {
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            if (digito1 != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }

            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            return digito2 == Character.getNumericValue(cpf.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }

    // Validação de nome (apenas letras e espaços)
    public static boolean isNomeValido(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        // Verifica se tem pelo menos 2 caracteres
        if (nome.trim().length() < 2) {
            return false;
        }

        // Verifica se contém apenas letras e espaços
        return APENAS_LETRAS_PATTERN.matcher(nome).matches();
    }

    // Validação de números
    public static boolean isNumeroValido(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        return APENAS_NUMEROS_PATTERN.matcher(numero).matches();
    }

    // Validação de data (não pode ser no passado para agendamentos)
    public static boolean isDataFuturaOuHoje(LocalDate data) {
        if (data == null) {
            return false;
        }
        return !data.isBefore(LocalDate.now());
    }

    // Validação de hora (dentro do expediente comercial)
    public static boolean isHoraComercial(LocalTime hora) {
        if (hora == null) {
            return false;
        }

        LocalTime inicioExpediente = LocalTime.of(8, 0);   // 8:00
        LocalTime fimExpediente = LocalTime.of(20, 0);     // 20:00

        return !hora.isBefore(inicioExpediente) && !hora.isAfter(fimExpediente);
    }

    // Validação de data e hora combinadas (não pode ser no passado)
    public static boolean isDataHoraFutura(LocalDateTime dataHora) {
        if (dataHora == null) {
            return false;
        }
        return dataHora.isAfter(LocalDateTime.now());
    }

    // Validação de preço (deve ser positivo)
    public static boolean isPrecoValido(Double preco) {
        return preco != null && preco > 0;
    }

    // Validação de quantidade (deve ser positiva)
    public static boolean isQuantidadeValida(Integer quantidade) {
        return quantidade != null && quantidade > 0;
    }

    // Formatar telefone
    public static String formatarTelefone(String telefone) {
        if (telefone == null) return "";

        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");

        if (telefoneLimpo.length() == 11) {
            return String.format("(%s) %s-%s",
                    telefoneLimpo.substring(0, 2),
                    telefoneLimpo.substring(2, 7),
                    telefoneLimpo.substring(7));
        } else if (telefoneLimpo.length() == 10) {
            return String.format("(%s) %s-%s",
                    telefoneLimpo.substring(0, 2),
                    telefoneLimpo.substring(2, 6),
                    telefoneLimpo.substring(6));
        } else {
            return telefone;
        }
    }

    // Formatar CPF
    public static String formatarCPF(String cpf) {
        if (cpf == null) return "";

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    cpfLimpo.substring(0, 3),
                    cpfLimpo.substring(3, 6),
                    cpfLimpo.substring(6, 9),
                    cpfLimpo.substring(9));
        } else {
            return cpf;
        }
    }

    // Validar e formatar data (String para LocalDate)
    public static LocalDate parseData(String dataStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Tenta vários formatos
            String[] partes = dataStr.split("[/-]");
            if (partes.length == 3) {
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int ano = Integer.parseInt(partes[2]);

                // Ajusta ano com 2 dígitos
                if (ano < 100) {
                    ano += 2000;
                }

                return LocalDate.of(ano, mes, dia);
            }
        } catch (Exception e) {
            // Ignora erros e retorna null
        }

        return null;
    }

    // Validar se string não é nula ou vazia
    public static boolean isStringValida(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // Truncar texto se muito longo
    public static String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";

        if (texto.length() <= maxLength) {
            return texto;
        }

        return texto.substring(0, maxLength - 3) + "...";
    }

    // Validar senha (mínimo 6 caracteres)
    public static boolean isSenhaValida(String senha) {
        if (senha == null || senha.length() < 6) {
            return false;
        }

        // Pode adicionar mais regras aqui
        return true;
    }
}