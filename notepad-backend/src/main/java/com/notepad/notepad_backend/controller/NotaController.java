package com.notepad.notepad_backend.controller;

import com.notepad.notepad_backend.model.Nota;
import com.notepad.notepad_backend.service.NotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class NotaController {
    
    @Autowired
    private NotaService notaService;

    @PostMapping
    public Nota criarNota(@RequestBody Nota nota) {
        // A lógica para associar a pasta e as tags será tratada no frontend
        // enviando os IDs corretos no objeto 'nota'.
        return notaService.salvar(nota);
    }

    @GetMapping
    public List<Nota> listarNotas() {
        return notaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nota> buscarNotaPorId(@PathVariable Long id) {
        return notaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint Mestre-Detalhe: Filtra notas por pasta
    @GetMapping("/por-pasta/{pastaId}")
    public ResponseEntity<List<Nota>> listarNotasPorPasta(@PathVariable Long pastaId) {
        try {
            List<Nota> notas = notaService.listarNotasPorPasta(pastaId);
            return ResponseEntity.ok(notas);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Nota> atualizarNota(@PathVariable Long id, @RequestBody Nota notaDetalhes) {
        return notaService.buscarPorId(id)
                .map(notaExistente -> {
                    notaExistente.setTitulo(notaDetalhes.getTitulo());
                    notaExistente.setConteudo(notaDetalhes.getConteudo());
                    notaExistente.setPasta(notaDetalhes.getPasta());
                    notaExistente.setTags(notaDetalhes.getTags());
                    return ResponseEntity.ok(notaService.salvar(notaExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNota(@PathVariable Long id) {
        if (notaService.buscarPorId(id).isPresent()) {
            notaService.deletar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
