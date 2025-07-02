package com.intesigroup.controller;

import com.intesigroup.model.Roles;
import com.intesigroup.model.RolesType;
import com.intesigroup.model.Users;
import com.intesigroup.repository.RolesRepository;
import com.intesigroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;

    //Inserimento utente
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        user.setRoles(resolveRolesFromRequest(user.getRoles()));
        return checkAndSave(user);
    }

    //Recupero lista utenti
    @GetMapping
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    //Recupero utente per ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<Users> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            // Restituisce l'utente trovato con HTTP 200
            return ResponseEntity.ok(user);
        } else {
            // Restituisce un messaggio descrittivo con HTTP 404
            String errorMessage = "Errore: utente con ID " + id + " non trovato.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    //Modifica dati utente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Users userDetails) {
        userDetails.setRoles(resolveRolesFromRequest(userDetails.getRoles()));
        Optional<Users> userOptional = userRepository.findById(id);

        //se non trovo l'utente con l'ID inserito
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Errore: utente con ID " + id + " non trovato.");
        }

        Users existingUser = userOptional.get();

        // Impedimento modifica email
        if (!existingUser.getEmail().equals(userDetails.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Errore: non è possibile modificare l'indirizzo email.");
        }

        //Aggiorna campi
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setName(userDetails.getName());
        existingUser.setSurname(userDetails.getSurname());
        existingUser.setTax_code(userDetails.getTax_code());

        //Gestione dei ruoli
        Set<Roles> resolvedRoles = new HashSet<>();
        for (Roles incomingRole : userDetails.getRoles()) {
            RolesType roleName = incomingRole.getNameRole();
            Optional<Roles> existingRole = rolesRepository.findByNameRole(roleName);
            if (existingRole.isPresent()) {
                resolvedRoles.add(existingRole.get());
            } else {
                return ResponseEntity.badRequest()
                        .body("Errore: ruolo " + roleName + " non valido.");
            }
        }
        existingUser.setRoles(resolvedRoles);

        Users updatedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    //Cancellazione utente
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        // cerco l'utente nel database
        Optional<Users> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            userRepository.delete(user);

            return ResponseEntity.ok("Utente con ID " + id + " cancellato con successo.");
        } else {
            // se l'utente non è presente restituisco errore
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Errore: utente con ID " + id + " non trovato.");
        }
    }

    private ResponseEntity<?> checkAndSave(Users user) {
        // Controllo mail
        ResponseEntity<?> mailCheck = checkMail(user);
        if (mailCheck != null) {
            return mailCheck;
        }

        // Controllo duplicati ruoli
        ResponseEntity<?> roleCheck = checkDuplicateRoles(user);
        if (roleCheck != null) {
            return roleCheck;
        }

        // Salvataggio
        Users savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    private ResponseEntity<?> checkDuplicateRoles(Users user) {
        Set<Roles> roles = user.getRoles();
        Set<RolesType> uniqueRoles = new HashSet<>();
        List<RolesType> duplicateRoles = new ArrayList<>();

        for (Roles role : roles) {
            RolesType roleName = role.getNameRole();
            if (!uniqueRoles.add(roleName)) {
                duplicateRoles.add(roleName);
            }
        }

        if (!duplicateRoles.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Errore: ruoli duplicati trovati -> " +
                            duplicateRoles.stream().map(Enum::name).collect(Collectors.joining(", ")));
        }
        return null; // OK: nessun errore
    }

    private ResponseEntity<?> checkMail(Users user) {
        Optional<Users> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Errore: esiste già un utente con questa email.");
        }
        return null; // OK: nessun errore
    }

    //prende SOLO i ruoli esistenti su DB
    private Set<Roles> resolveRolesFromRequest(Set<Roles> requestedRoles) {
        Set<Roles> resolvedRoles = new HashSet<>();

        for (Roles role : requestedRoles) {
            RolesType roleType = role.getNameRole();

            Optional<Roles> optionalRole = rolesRepository.findByNameRole(roleType);
            if (optionalRole.isPresent()) {
                Roles existingRole = optionalRole.get();
                resolvedRoles.add(existingRole);
            } else {
                throw new RuntimeException("Ruolo non trovato: " + roleType);
            }
        }

        return resolvedRoles;
    }
}
