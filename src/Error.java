package src;
public class Error {
    public interface ErrorHandler {
        void handleError(ErrorTypes type, String lexema);
    }

    public enum ErrorTypes {
        CARACTERE_INVALIDO_LEXICO, LEXEMA_NAO_IDENTIFICADO_LEXICO, FIM_DE_ARQUIVO_NAO_ESPERADO_LEXICO,
        TOKEN_NAO_ESPERADO_SINTATICO, FIM_DE_ARQUIVO_NAO_ESPERADO_SINTATICO
    }
}
