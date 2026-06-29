package br.com.mensageria.app;

import br.com.mensageria.dao.ConversaDAO;
import br.com.mensageria.dao.MensagemDAO;
import br.com.mensageria.dao.UsuarioDAO;
import br.com.mensageria.model.Conversa;
import br.com.mensageria.model.Mensagem;
import br.com.mensageria.model.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ConversaDAO conversaDAO = new ConversaDAO();
    private final MensagemDAO mensagemDAO = new MensagemDAO();

    public static void main(String[] args) {
        new Main().executar();
    }

    private void executar() {
        System.out.println("=== Sistema de Mensageria ===");
        int opcao;

        do {
            exibirMenuPrincipal();
            opcao = lerInteiro("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1:
                        menuUsuarios();
                        break;
                    case 2:
                        menuConversas();
                        break;
                    case 3:
                        menuMensagens();
                        break;
                    case 0:
                        System.out.println("Programa encerrado.");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (SQLException e) {
                System.err.println("Erro de banco de dados: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Operação inválida: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n1 - CRUD de usuários");
        System.out.println("2 - CRUD de conversas");
        System.out.println("3 - CRUD de mensagens");
        System.out.println("0 - Sair");
    }

    private void menuUsuarios() throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== Usuários ===");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Consultar por ID");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Excluir");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");

            switch (opcao) {
                case 1: cadastrarUsuario(); break;
                case 2: listarUsuarios(); break;
                case 3: consultarUsuario(); break;
                case 4: atualizarUsuario(); break;
                case 5: excluirUsuario(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void cadastrarUsuario() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setNome(lerTextoObrigatorio("Nome: "));
        usuario.setTelefone(lerTextoObrigatorio("Telefone: "));
        usuario.setEmail(lerTextoOpcional("E-mail (opcional): "));
        usuario.setStatus(lerOpcao("Status (ATIVO/INATIVO): ", "ATIVO", "INATIVO"));

        int id = usuarioDAO.inserir(usuario);
        System.out.println("Usuário cadastrado com ID " + id + ".");
    }

    private void listarUsuarios() throws SQLException {
        List<Usuario> usuarios = usuarioDAO.listar();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    private void consultarUsuario() throws SQLException {
        int id = lerInteiro("ID do usuário: ");
        Optional<Usuario> usuario = usuarioDAO.buscarPorId(id);
        System.out.println(usuario.map(Object::toString).orElse("Usuário não encontrado."));
    }

    private void atualizarUsuario() throws SQLException {
        int id = lerInteiro("ID do usuário: ");
        Optional<Usuario> existente = usuarioDAO.buscarPorId(id);
        if (existente.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        Usuario usuario = existente.get();
        usuario.setNome(lerTextoObrigatorio("Novo nome: "));
        usuario.setTelefone(lerTextoObrigatorio("Novo telefone: "));
        usuario.setEmail(lerTextoOpcional("Novo e-mail (opcional): "));
        usuario.setStatus(lerOpcao("Status (ATIVO/INATIVO): ", "ATIVO", "INATIVO"));

        System.out.println(usuarioDAO.atualizar(usuario)
                ? "Usuário atualizado."
                : "Nenhum usuário foi atualizado.");
    }

    private void excluirUsuario() throws SQLException {
        int id = lerInteiro("ID do usuário: ");
        System.out.println(usuarioDAO.excluir(id)
                ? "Usuário excluído."
                : "Usuário não encontrado.");
    }

    private void menuConversas() throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== Conversas ===");
            System.out.println("1 - Criar conversa");
            System.out.println("2 - Listar conversas");
            System.out.println("3 - Consultar conversa e participantes");
            System.out.println("4 - Atualizar conversa");
            System.out.println("5 - Adicionar participante");
            System.out.println("6 - Remover participante");
            System.out.println("7 - Excluir conversa");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");

            switch (opcao) {
                case 1: criarConversa(); break;
                case 2: listarConversas(); break;
                case 3: consultarConversa(); break;
                case 4: atualizarConversa(); break;
                case 5: adicionarParticipante(); break;
                case 6: removerParticipante(); break;
                case 7: excluirConversa(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void criarConversa() throws SQLException {
        Conversa conversa = new Conversa();
        conversa.setTipo(lerOpcao("Tipo (PRIVADA/GRUPO): ", "PRIVADA", "GRUPO"));
        conversa.setTitulo(lerTextoOpcional("Título (obrigatório para grupo): "));

        if ("GRUPO".equals(conversa.getTipo()) &&
                (conversa.getTitulo() == null || conversa.getTitulo().isBlank())) {
            throw new IllegalArgumentException("Conversas em grupo precisam de título.");
        }

        int quantidade = "PRIVADA".equals(conversa.getTipo())
                ? 2
                : lerInteiroPositivo("Quantidade de participantes: ");

        List<Integer> participantes = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            int idUsuario = lerInteiro("ID do participante " + i + ": ");
            if (participantes.contains(idUsuario)) {
                throw new IllegalArgumentException("O mesmo usuário não pode aparecer duas vezes.");
            }
            participantes.add(idUsuario);
        }

        int id = conversaDAO.inserirComParticipantes(conversa, participantes);
        System.out.println("Conversa criada com ID " + id + ".");
    }

    private void listarConversas() throws SQLException {
        List<Conversa> conversas = conversaDAO.listar();
        if (conversas.isEmpty()) {
            System.out.println("Nenhuma conversa cadastrada.");
            return;
        }
        conversas.forEach(System.out::println);
    }

    private void consultarConversa() throws SQLException {
        int id = lerInteiro("ID da conversa: ");
        Optional<Conversa> conversa = conversaDAO.buscarPorId(id);
        if (conversa.isEmpty()) {
            System.out.println("Conversa não encontrada.");
            return;
        }

        System.out.println(conversa.get());
        System.out.println("Participantes:");
        List<String> participantes = conversaDAO.listarParticipantes(id);
        if (participantes.isEmpty()) {
            System.out.println("Nenhum participante.");
        } else {
            participantes.forEach(System.out::println);
        }
    }

    private void atualizarConversa() throws SQLException {
        int id = lerInteiro("ID da conversa: ");
        Optional<Conversa> existente = conversaDAO.buscarPorId(id);
        if (existente.isEmpty()) {
            System.out.println("Conversa não encontrada.");
            return;
        }

        Conversa conversa = existente.get();
        conversa.setTipo(lerOpcao("Novo tipo (PRIVADA/GRUPO): ", "PRIVADA", "GRUPO"));
        conversa.setTitulo(lerTextoOpcional("Novo título: "));

        if ("GRUPO".equals(conversa.getTipo()) &&
                (conversa.getTitulo() == null || conversa.getTitulo().isBlank())) {
            throw new IllegalArgumentException("Conversas em grupo precisam de título.");
        }

        System.out.println(conversaDAO.atualizar(conversa)
                ? "Conversa atualizada."
                : "Nenhuma conversa foi atualizada.");
    }

    private void adicionarParticipante() throws SQLException {
        int idConversa = lerInteiro("ID da conversa: ");
        int idUsuario = lerInteiro("ID do usuário: ");
        String funcao = lerOpcao("Função (MEMBRO/ADMINISTRADOR): ", "MEMBRO", "ADMINISTRADOR");
        conversaDAO.adicionarParticipante(idConversa, idUsuario, funcao);
        System.out.println("Participante adicionado.");
    }

    private void removerParticipante() throws SQLException {
        int idConversa = lerInteiro("ID da conversa: ");
        int idUsuario = lerInteiro("ID do usuário: ");
        System.out.println(conversaDAO.removerParticipante(idConversa, idUsuario)
                ? "Participante removido."
                : "Participação não encontrada.");
    }

    private void excluirConversa() throws SQLException {
        int id = lerInteiro("ID da conversa: ");
        System.out.println(conversaDAO.excluir(id)
                ? "Conversa e suas mensagens foram excluídas."
                : "Conversa não encontrada.");
    }

    private void menuMensagens() throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== Mensagens ===");
            System.out.println("1 - Enviar mensagem");
            System.out.println("2 - Listar mensagens de uma conversa");
            System.out.println("3 - Consultar mensagem por ID");
            System.out.println("4 - Editar mensagem");
            System.out.println("5 - Remover mensagem logicamente");
            System.out.println("6 - Excluir mensagem fisicamente");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");

            switch (opcao) {
                case 1: enviarMensagem(); break;
                case 2: listarMensagens(); break;
                case 3: consultarMensagem(); break;
                case 4: editarMensagem(); break;
                case 5: removerMensagem(); break;
                case 6: excluirMensagem(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void enviarMensagem() throws SQLException {
        Mensagem mensagem = new Mensagem();
        mensagem.setIdUsuario(lerInteiro("ID do autor: "));
        mensagem.setIdConversa(lerInteiro("ID da conversa: "));
        mensagem.setConteudo(lerTextoObrigatorio("Conteúdo: "));
        int id = mensagemDAO.inserir(mensagem);
        System.out.println("Mensagem enviada com ID " + id + ".");
    }

    private void listarMensagens() throws SQLException {
        int idConversa = lerInteiro("ID da conversa: ");
        List<Mensagem> mensagens = mensagemDAO.listarPorConversa(idConversa);
        if (mensagens.isEmpty()) {
            System.out.println("Nenhuma mensagem encontrada.");
            return;
        }
        mensagens.forEach(System.out::println);
    }

    private void consultarMensagem() throws SQLException {
        int id = lerInteiro("ID da mensagem: ");
        Optional<Mensagem> mensagem = mensagemDAO.buscarPorId(id);
        System.out.println(mensagem.map(Object::toString).orElse("Mensagem não encontrada."));
    }

    private void editarMensagem() throws SQLException {
        int id = lerInteiro("ID da mensagem: ");
        String conteudo = lerTextoObrigatorio("Novo conteúdo: ");
        System.out.println(mensagemDAO.atualizarConteudo(id, conteudo)
                ? "Mensagem editada."
                : "Mensagem não encontrada ou já removida.");
    }

    private void removerMensagem() throws SQLException {
        int id = lerInteiro("ID da mensagem: ");
        System.out.println(mensagemDAO.removerLogicamente(id)
                ? "Mensagem marcada como removida."
                : "Mensagem não encontrada.");
    }

    private void excluirMensagem() throws SQLException {
        int id = lerInteiro("ID da mensagem: ");
        System.out.println(mensagemDAO.excluirFisicamente(id)
                ? "Mensagem excluída fisicamente."
                : "Mensagem não encontrada.");
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private int lerInteiroPositivo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor > 0) {
                return valor;
            }
            System.out.println("O valor deve ser maior que zero.");
        }
    }

    private String lerTextoObrigatorio(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo é obrigatório.");
        }
    }

    private String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? null : texto;
    }

    private String lerOpcao(String mensagem, String... valoresPermitidos) {
        while (true) {
            String valor = lerTextoObrigatorio(mensagem).toUpperCase();
            for (String permitido : valoresPermitidos) {
                if (permitido.equals(valor)) {
                    return valor;
                }
            }
            System.out.println("Valor inválido.");
        }
    }
}
