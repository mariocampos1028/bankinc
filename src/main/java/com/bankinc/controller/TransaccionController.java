package com.bankinc.controller;

import com.bankinc.dto.AnularTransaccionRequest;
import com.bankinc.dto.ApiResponse;
import com.bankinc.dto.TransaccionRequest;
import com.bankinc.entity.Transaccion;
import com.bankinc.service.TransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Transaccion>> realizarTransaccion(@RequestBody TransaccionRequest request) {
        ApiResponse<Transaccion> response = transaccionService.crearTransaccion(request.getCardId(), request.getPrice());
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaccion>> obtenerTransaccion(@PathVariable Long transactionId) {
        ApiResponse<Transaccion> response = transaccionService.obtenerTransaccion(transactionId);
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/anulation")
    public ResponseEntity<ApiResponse<Transaccion>> anularTransaccion(@RequestBody AnularTransaccionRequest request){
        ApiResponse<Transaccion> response = transaccionService.anularTransaccion(request.getCardId(), request.getTransactionId());
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }


}
