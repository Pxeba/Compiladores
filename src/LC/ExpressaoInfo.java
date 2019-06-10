package LC;

import java.awt.print.Book;

import static LC.AnalisadorSintatico.*;

class ExpressaoInfo
{
    Simbolo.Tipos tipo;
    String valor;
    int tamanho;
    boolean error = false;
    boolean isString = false;

    ExpressaoInfo ( Simbolo.Tipos tipo , String valor , int tamanho )
    {
        this.tipo = tipo;
        this.valor = valor;
        this.tamanho = tamanho;
    }

    boolean isInteiro() {
        return ( tipo == Simbolo.Tipos.INT  && tamanho == 0);
    }

    boolean isCharacter() {
        return ( tipo == Simbolo.Tipos.CHAR && tamanho == 0);
    }

    boolean isArrayDeCharacter() {
        return ( tipo == Simbolo.Tipos.CHAR && tamanho > 0);
    }

    boolean isLogico() {
        return ( tipo == Simbolo.Tipos.BOOL );
    }

    public boolean isString() {
        return isString;
    }

    void negar ( )
    {
        if ( tipo != Simbolo.Tipos.BOOL ) // Se o tipo que estamos operando nao e' logico
        {
            error = true;
        }
        else
        {
            if ( valor.equals("0") )
                valor = "1";
            else
                valor = "0";
        }
    }
}
