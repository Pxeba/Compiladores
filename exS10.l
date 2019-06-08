/* integer sem var */
Var integer n;
var integer x;
var Char nome[40];
Const MAXITER=10;

/* Programa Principal */

  write("Digite seu nome: ");
  readln(nome);
  For n=1 to MAXITER do {
    writeln("Ola' ",nome);
  }