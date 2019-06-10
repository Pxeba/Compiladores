/* 0x15, deve funcionar  */
Var integer n;
Char nome[40];
Const MAXITER= 0x15;

/* Programa Principal */

  write("Digite seu nome: ");
  readln(nome);
  For n=1 to MAXITER do {
    writeln("Ola' ",nome);
  }