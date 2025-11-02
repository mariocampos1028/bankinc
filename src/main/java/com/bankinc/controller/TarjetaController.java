package com.bankinc.controller;

import com.bankinc.dto.ActivarTarjetaRequest;
import com.bankinc.dto.ApiResponse;
import com.bankinc.dto.BalanceRequest;
import com.bankinc.entity.Tarjeta;
import com.bankinc.service.TarjetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
public class TarjetaController {

    private final TarjetaService tarjetaService;

    public TarjetaController(TarjetaService tarjetaService) {
        this.tarjetaService = tarjetaService;
    }

    /**
     * Generar número de tarjeta
     */
    @GetMapping("/{productId}/number")
    public ResponseEntity<ApiResponse<Tarjeta>> generarTarjeta(
            @PathVariable String productId,
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        ApiResponse<Tarjeta> response = tarjetaService.generarTarjeta(productId, firstName, lastName);
        if("ERROR".equals(response.getStatus())){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<Tarjeta>> activarTarjeta(@RequestBody ActivarTarjetaRequest activarTarjeta){
        ApiResponse<Tarjeta> response = tarjetaService.activarTarjeta(activarTarjeta.getCardId());
        if("ERROR".equals(response.getStatus())){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Tarjeta>> bloquearTarjeta(@RequestParam String cardId){
        ApiResponse<Tarjeta> response = tarjetaService.bloquearTarjeta(cardId);
        if("ERROR".equals(response.getStatus())){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/balance")
    public ResponseEntity<ApiResponse<Tarjeta>> cargarTarjeta(@RequestBody BalanceRequest balanceRequest){
        ApiResponse<Tarjeta> response = tarjetaService.recargarTarjeta(balanceRequest.getCardId(), balanceRequest.getBalance());
        if("ERROR".equals(response.getStatus())){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/balance/{cardId}")
    public ResponseEntity<ApiResponse<Tarjeta>> obtenerBalance(@PathVariable String cardId){
        ApiResponse<Tarjeta> response = tarjetaService.obtenerBalance(cardId);
        if("ERROR".equals(response.getStatus())){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }




}
