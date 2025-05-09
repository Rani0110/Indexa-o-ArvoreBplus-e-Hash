
import java.io.*;
import java.util.ArrayList;

public class CRUD {

    //Caminho para o arquivo 
    private String nomeArquivo = "./DadosTeste/netflix1.db";

    //Construtor
    public CRUD() {
        try (RandomAccessFile arq = new RandomAccessFile(nomeArquivo, "rw")) {
            if (arq.length() == 0) {
                arq.seek(0);
                arq.writeInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Importa dados de um csv e grava no arquivo binario
    public void importarCSVParaBinario() {
        String csv = "./DadosTeste/netflix1.csv";
        int IDaux = -1;
        BufferedReader br = null;

        try (RandomAccessFile arq = new RandomAccessFile(this.nomeArquivo, "rw")) {
            if (!new File(this.nomeArquivo).isFile()) return;

            br = new BufferedReader(new FileReader(csv));
            String linha;
            br.readLine();
            arq.seek(4); //Pula os 4 primeiros bytes

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                //Divide os campos do csv e ignora as aspas duplas
                String[] infos = linha.split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)");
                Netflix nf = new Netflix();

                if (infos[1].equals("TV Show")) continue;

                //preenche os campos do objeto
                nf.setIdFilme(infos[0]);
                nf.setTipo(infos[1]);
                nf.setTitulo(infos[2]);
                nf.setDiretor(infos[3]);
                nf.setPais(infos[4]);
                nf.setData(infos[5]);
                nf.setDuracao(infos[8]);
                nf.setGenero(infos[9]);

                //Atualzia o ID maximo
                if (IDaux < nf.getIntIdFilme()) IDaux = nf.getIntIdFilme();

                //Escrevendo no arquivo
                byte[] dataBytes = nf.toByteArray();
                arq.writeByte(' '); //Lapide viva
                arq.writeShort(dataBytes.length);
                arq.write(dataBytes);
            }

            //Escreve o ID maximo no inicio do arquivo
            arq.seek(0);
            arq.writeInt(IDaux);

        } catch (IOException e) {
            e.getMessage();
        }
    }

    //CREATE
    public void escreveNetflixBin(Netflix nf, TreeBplus arvore, HashEstendido hash) {
        if (this.nomeArquivo.isEmpty()) return;

        try (RandomAccessFile arq = new RandomAccessFile(this.nomeArquivo, "rw")) {
            arq.seek(0);
            int IDant = arq.readInt(); //Le o ID maximo
            long ultimoEndereco = arq.length(); //Pega o ultimo endereco
            arq.seek(ultimoEndereco);

            //Atribui o novo ID ao registro
            nf.setIdFilme("s" + String.valueOf(IDant + 1));

            //Escreve os dados no arquivo
            byte[] dataBytes = nf.toByteArray();
            arq.writeByte(' ');
            arq.writeShort(dataBytes.length);
            arq.write(dataBytes);

            //Atualiza o ID maximo
            arq.seek(0);
            arq.writeInt(IDant + 1);

            //Insere o ID e o endereco na arvore
            int idInt = nf.getIntIdFilme();
            arvore.inserir(new PonteiroArquivo(idInt, ultimoEndereco));
            
            //Insere o ID e o endereco na arvore
            hash.inserir(idInt, ultimoEndereco);

            System.out.println("\nRegistro gravado!");
            System.out.println(nf);

            arq.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //UPTADE
    public void atualizar(Netflix nf, TreeBplus arvore, HashEstendido hash) throws IOException {
        if (this.nomeArquivo.isEmpty()) return;

        try (RandomAccessFile arq = new RandomAccessFile(this.nomeArquivo, "rw")) {
            arq.seek(4); //Pula o ID maximo

            while (arq.getFilePointer() < arq.length()) {
                byte lapide = arq.readByte();
                short tamanho = arq.readShort();
                long pos = arq.getFilePointer();

                if (lapide == ' ') { //Verifica se a lapide esta viva
                    byte[] dados = new byte[tamanho];
                    arq.read(dados);

                    Netflix registroAntigo = new Netflix();
                    registroAntigo.fromByteArray(dados);

                    if (registroAntigo.getIdFilme().equals(nf.getIdFilme())) {
                        byte[] novoRegistro = nf.toByteArray();
                        int novoTamanho = novoRegistro.length;

                        if (novoTamanho <= tamanho) { //Verifica se o novo registro cabe no espaco antigo e sobrescreve
                           
                            arq.seek(pos);
                            arq.write(novoRegistro);
                            System.out.println("\nRegistro atualizado com sucesso!\n");
                        
                        } else { //Se for maior escreve no fim do arquivo
                            
                            //Marca o registro antigo como antigo
                            arq.seek(pos - 3);
                            arq.writeByte('-');

                            //Escreve no final
                            long novoEndereco = arq.length();
                            arq.seek(novoEndereco);
                            arq.writeByte(' ');
                            arq.writeShort(novoTamanho);
                            arq.write(novoRegistro);
                            System.out.println("\nNovo registro criado no final do arquivo!");

                            //Atualizando os indices na arvore
                            System.out.println("Atualizado na arvore.");
                            PonteiroArquivo antigoPt = arvore.quebragalho(nf.getIntIdFilme());
                            antigoPt.setEndereco(novoEndereco);

                            //Atualiza os indices no hash
                            System.out.println("Atualizado no hash.");
                            hash.atualizar(nf.getIntIdFilme(), novoEndereco);
                            
                        }
                        return;
                    }
                } else {
                    arq.skipBytes(tamanho); //Pula registro deletado
                }
            }
            System.out.println("Filme com ID " + nf.getIdFilme() + " não encontrado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //DELETE
    public void deletar(String ID, TreeBplus arvore, HashEstendido hash) throws IOException {
        if (this.nomeArquivo.isEmpty()) return;

        try (RandomAccessFile arq = new RandomAccessFile(this.nomeArquivo, "rw")) {
            arq.seek(0);
            int IDmax = arq.readInt();

            //Deletando o indice na arvore
            PonteiroArquivo elementoDeletar = arvore.excluirElemento(Integer.parseInt(ID.replace("s", "0")));
            if (elementoDeletar == null) return;

            arq.seek(elementoDeletar.getEndereco());
            arq.writeByte('-');

            //Deletando o indice no hash
            hash.remover(elementoDeletar.getID());

            //Atualiza o indice maximo se necessario
            if (elementoDeletar.getID() == IDmax) {
                arq.seek(0);
                arq.writeInt(IDmax - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Gerando arquivo Hash
    public HashEstendido gerarArquivoIDsHah(String arquivoID) throws IOException {
        HashEstendido hash = new HashEstendido();

        try (RandomAccessFile arq = new RandomAccessFile(this.nomeArquivo, "r");
             RandomAccessFile arqIndice = new RandomAccessFile(arquivoID, "rw")) {

            arq.seek(4);
            while (arq.getFilePointer() < arq.length()) {
                long endereco = arq.getFilePointer();
                byte lapide = arq.readByte();
                short tamanho = arq.readShort();

                if (lapide == ' ') {
                    byte[] dados = new byte[tamanho];
                    arq.read(dados);
                    Netflix nf = new Netflix();
                    nf.fromByteArray(dados);
                    int id = nf.getIntIdFilme();
                    hash.inserir(id, endereco);
                    arqIndice.writeInt(id);
                    arqIndice.writeLong(endereco);
                } else {
                    arq.skipBytes(tamanho);
                }
            }
        }
        System.out.println("Arquivo de ID hash gerado");
        return hash;
    }

    //Gerando arquivo arvore
    public TreeBplus gerarArquivoIDs(String arquivoID, int ordem) throws IOException {
        TreeBplus tree = new TreeBplus(ordem);

        try (RandomAccessFile arq = new RandomAccessFile(this.nomeArquivo, "r");
             RandomAccessFile arqIndice = new RandomAccessFile(arquivoID, "rw")) {

            arq.seek(4);
            while (arq.getFilePointer() < arq.length()) {
                long endereco = arq.getFilePointer();
                byte lapide = arq.readByte();
                short tamanho = arq.readShort();

                if (lapide == ' ') {
                    byte[] dados = new byte[tamanho];
                    arq.read(dados);
                    Netflix nf = new Netflix();
                    nf.fromByteArray(dados);
                    int id = nf.getIntIdFilme();
                    tree.inserir(new PonteiroArquivo(id, endereco));
                    arqIndice.writeInt(id);
                    arqIndice.writeLong(endereco);
                } else {
                    arq.skipBytes(tamanho);
                }
            }
        }
        System.out.println("Arquivo de ID gerado");
        return tree;
    }

    //Funcao que busca o endereco do registro
    public void buscaEndereçoRegistro(long endereco) throws IOException {
        try (RandomAccessFile arq = new RandomAccessFile(nomeArquivo, "r")) {
            arq.seek(endereco);

            byte lapide = arq.readByte();
            short tamanho = arq.readShort();
            byte[] dados = new byte[tamanho];
            arq.read(dados);

            Netflix nf = new Netflix();
            nf.fromByteArray(dados);
            System.out.println(nf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}