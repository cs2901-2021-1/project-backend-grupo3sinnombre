package com.example.restbackend.model.usuario;

import com.example.restbackend.customException.CustomException;
import com.example.restbackend.customException.ResourceNotFoundException;
import com.example.restbackend.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class UsuarioService {
    static final Logger logger = Logger.getLogger(UsuarioService.class.getName());
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getUsuarios(){
        return usuarioRepository.findAll();
    }

    public Usuario findOneById(Long id) {
        Optional<Usuario> userOptional = usuarioRepository.findById(id);
        if (userOptional.isPresent())
            return userOptional.get();
        else
            return null;
    }

    public Usuario findOneByEmail(String email) {
        Optional<Usuario> userOptional = usuarioRepository.findUsuarioByEmail(email);
        if (userOptional.isPresent())
            return userOptional.get();
        else
            return null;
    }

    public Usuario findOneByGoogleId(String googleId){
        Optional<Usuario> userOptional = usuarioRepository.findUsuarioByGoogleId(googleId);
        if (userOptional.isPresent())
            return userOptional.get();
        else
            return null;
    }

    public void addNewUsuario(Usuario user) {
        Optional<Usuario> userOptional = usuarioRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            throw new CustomException("Este usuario ya se encuentra registrado");
        }
        usuarioRepository.save(user);
    }

    @Transactional
    public void updateUsuarioRole(String email, String role) {
        var user = usuarioRepository.findUsuarioByEmail(email)
                .orElseThrow(() -> new CustomException("La persona con el email: " + email + ", no existe."));
        user.setRole(role);
    }

    public void deleteUsuario(String email){
        usuarioRepository.deleteUsuarioByEmail(email);
    }

    public UserDetails loadUserById(String userGoogleId) {
        Usuario usuario = usuarioRepository.findUsuarioByGoogleId(userGoogleId).orElseThrow(
                () -> new ResourceNotFoundException("User", "Google id", userGoogleId)
        );
        return UserPrincipal.create(usuario);
    }
}