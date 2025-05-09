import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        
        CRUD fh = new CRUD();
        System.out.println("Digite a ordem da Árvore B+: ");
        Scanner scan = new Scanner(System.in);
        int ordem = Integer.parseInt(scan.nextLine());
        TreeBplus tree = new TreeBplus(ordem);
        HashEstendido hash = new HashEstendido();


        fh.importarCSVParaBinario();
        tree = fh.gerarArquivoIDs("./DadosTeste/idsArvore.db", ordem); // Gera o novo arquivo apenas com IDs arvore B+
        hash = fh.gerarArquivoIDsHah("./DadosTeste/idsHash.db"); // Gera o novo arquivo apenas com IDs hash
        
        
        Main main = new Main();
        main.menu(fh, tree, hash);
    
    }

    public void menu(CRUD metodos, TreeBplus arvore, HashEstendido hash) throws IOException {

        
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Criar registro");
            System.out.println("2. Buscar 1 registro");
            System.out.println("3. Buscar mais de 1 registro");
            System.out.println("4. Atualizar registro");
            System.out.println("5. Deletar registro");
            System.out.println("6. Sair");
            System.out.print("Digite sua escolha: ");
            int opcao = Integer.parseInt(scan.nextLine());

            switch (opcao) {
                // Criar um novo registro
                case 1:
                    System.out.println("\nDigite os atributos necessarios:");
                    System.out.println("\nTipo: ");
                    String tipo = scan.nextLine();
                    System.out.println("\nTitulo");
                    String titulo = scan.nextLine();
                    System.out.println("\nDiretor");
                    String diretor = scan.nextLine();
                    System.out.println("\nPaís");
                    String pais = scan.nextLine();
                    System.out.println("\nDuração");
                    String duracao = scan.nextLine();

                    System.out.println("\nData (mm/dd/aaaa): ");
                    String data = scan.nextLine();

                    System.out.println("\nDigite a quantidade de generos: ");
                    int qtd = Integer.parseInt(scan.nextLine());

                    System.out.println("\nDigite os generos: ");
                    ArrayList<String> genero = new ArrayList<String>();
                    for (int i = 0; i < qtd; i++) {
                        genero.add(scan.nextLine());
                    }

                    Netflix nf = new Netflix();
                    nf.setTipo(tipo);
                    nf.setTitulo(titulo);
                    nf.setDiretor(diretor);
                    nf.setPais(pais);
                    nf.setDuracao(Integer.parseInt(duracao));
                    nf.setData(data);
                    nf.setGenero(genero);

                    metodos.escreveNetflixBin(nf, arvore, hash);
                    break;

                case 2:
                    // Buscando ID

                    System.out.println("\nDigite o numero do ID: ");
                    String id = scan.nextLine().replace("s", "0");
                    int idInt = Integer.parseInt(id);
                   
                      
                    //Busca na arvore
                    System.out.println("\nBusca na arvore:");
                    PonteiroArquivo aux = arvore.buscaElemento(idInt);
                    if(aux != null)
                        metodos.buscaEndereçoRegistro(aux.getEndereco());
                    else{
                        System.out.println("ID s" + idInt + " não encontrado na arvore.");
                        break;
                    }
        

                    //Busca no Hash Estendido
                    System.out.println("\nBusca no Hash Estendido:");
                    long enderecoHash = hash.buscar(idInt);
                    if (enderecoHash != -1){
                        metodos.buscaEndereçoRegistro(enderecoHash);
                    }
                    else{
                        System.out.println("ID s" + idInt + " não encontrado no Hash.");
                    }
                    
                    break;         
                    

                case 3:
                    // Buscando IDs
                    System.out.println("\nDigite a quantidade de registros que deseja pesquisar: ");
                    int qtd2 = Integer.parseInt(scan.nextLine());
                    ArrayList<String> Ids = new ArrayList<String>();
                    System.out.println("\nDigite os IDs: ");

                    for (int i = 0; i < qtd2; i++) {
                        Ids.add(scan.nextLine());
                    }
                    
                    // Varios IDs arvore
                    // Busca na arvore
                    System.out.println("\nBusca na arvore:");
                    PonteiroArquivo[] elementos = arvore.buscaVariosElementos(Ids);
                    for (PonteiroArquivo no : elementos) {
                        if(no != null)
                            metodos.buscaEndereçoRegistro(no.getEndereco());
                        else{
                            System.out.println("Elemento não encontrado na arvore.");
                            break;
                        }
                    }
                    

                    // Varios IDs hash
                    // Busca no Hash Estendido
                    System.out.println("\nBusca no Hash Estendido:");
                    long[] enderecosHash = hash.buscaVariosElementos(Ids);
                    for (long endereco : enderecosHash) {
                        if (endereco != -1) {
                            metodos.buscaEndereçoRegistro(endereco);
                        } else {
                            System.out.println("Elemento não encontrado no Hash.");
                        }
                    }
                    break;

                case 4:
                    // Atualizando registro
                    System.out.println("\nDigite o numero do ID: ");
                    String id2 = scan.nextLine();
                    System.out.println("\nDigite os atributos para a atualização:");
                    System.out.println("\nTipo: ");
                    String tipo2 = scan.nextLine();
                    System.out.println("\nTitulo");
                    String titulo2 = scan.nextLine();
                    System.out.println("\nDiretor");
                    String diretor2 = scan.nextLine();
                    System.out.println("\nPaís");
                    String pais2 = scan.nextLine();
                    System.out.println("\nDuração");
                    String duracao2 = scan.nextLine();
                    System.out.println("\nData (mm/dd/aaaa): ");
                    String data2 = scan.nextLine();
                    System.out.println("\nDigite a quantidade de generos: ");
                    int qtd3 = Integer.parseInt(scan.nextLine());

                    System.out.println("\nDigite os generos: ");
                    ArrayList<String> genero2 = new ArrayList<String>();
                    for (int i = 0; i < qtd3; i++) {
                        genero2.add(scan.nextLine());
                    }

                    Netflix netflix = new Netflix();

                    netflix.setIdFilme(id2);
                    netflix.setTipo(tipo2);
                    netflix.setTitulo(titulo2);
                    netflix.setDiretor(diretor2);
                    netflix.setPais(pais2);
                    netflix.setDuracao(Integer.parseInt(duracao2));
                    netflix.setData(data2);
                    netflix.setGenero(genero2);

                    //Atualiza na arvore e no hash ao mesmo tempo
                    metodos.atualizar(netflix, arvore, hash);

                    break;

                case 5:
                    // Delete
                    System.out.println("\nDigite o ID do registro que deseja deletar: ");
                    String ID = scan.nextLine().replace("s", "0");
                    int IdInt = Integer.parseInt(ID);

                    //Deleta na arvore e no hash ao mesmo tempo
                    metodos.deletar(ID, arvore, hash);
                    System.out.println("Registro excluido na arvore e no hash");
                    break;

                case 6:
                    // Fim
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}
