// Todas as exceções personalizadas em um arquivo

class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário com ID " + id + " não encontrado.");
    }
}

class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Email " + email + " já está cadastrado.");
    }
}

class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() { super("Email ou senha inválidos."); }
}

class ContaSuspensaException extends RuntimeException {
    public ContaSuspensaException() { super("Conta suspensa. Entre em contato com o suporte."); }
}

class ContaCanceladaException extends RuntimeException {
    public ContaCanceladaException() { super("Conta cancelada."); }
}
