package com.example.battleship.web;

import com.example.battleship.dto.GameResponseDTO;
import com.example.battleship.service.GameService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.io.ByteArrayInputStream;
import java.util.List;

import org.msgpack.core.MessagePack;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameService gameService;

    @Test
    void createGame_returns201() throws Exception {
        GameResponseDTO response = new GameResponseDTO(1L, 10, "PENDING",
                LocalDateTime.now(), List.of(), List.of());
        when(gameService.createGame(any())).thenReturn(response);

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void listGames_returns200() throws Exception {
        var page = new PageImpl<>(List.of());
        when(gameService.listGames(any(Specification.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getGame_returns200() throws Exception {
        GameResponseDTO response = new GameResponseDTO(1L, 10, "IN_PROGRESS",
                LocalDateTime.now(), List.of(), List.of());
        when(gameService.getGame(1L)).thenReturn(response);

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getGame_returnsMessagePack_whenClientAcceptsIt() throws Exception {
        GameResponseDTO response = new GameResponseDTO(1L, 10, "IN_PROGRESS",
                LocalDateTime.of(2026, 8, 17, 10, 0), List.of(), List.of());
        when(gameService.getGame(1L)).thenReturn(response);

        byte[] body = mockMvc.perform(get("/api/games/1")
                        .accept("application/msgpack"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/msgpack"))
                .andReturn().getResponse().getContentAsByteArray();

        try (var unpacker = MessagePack.newDefaultUnpacker(new ByteArrayInputStream(body))) {
            var value = unpacker.unpackValue().asMapValue();
            var id = value.map().entrySet().stream()
                    .filter(entry -> entry.getKey().asStringValue().asString().equals("id"))
                    .findFirst()
                    .orElseThrow()
                    .getValue();
            org.junit.jupiter.api.Assertions.assertEquals(1, id.asIntegerValue().asInt());
        }
    }

    @Test
    void getGame_returns406_whenNoRepresentationIsAcceptable() throws Exception {
        GameResponseDTO response = new GameResponseDTO(1L, 10, "IN_PROGRESS",
                LocalDateTime.now(), List.of(), List.of());
        when(gameService.getGame(1L)).thenReturn(response);

        mockMvc.perform(get("/api/games/1")
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void getGame_notFound_returns404() throws Exception {
        when(gameService.getGame(999L)).thenThrow(new com.example.battleship.domain.exceptions.GameNotFoundException(999L));

        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void createGame_invalidBoardSize_returns400() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boardSize\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createGame_returns415_whenRequestContentTypeIsNotSupported() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<game><boardSize>10</boardSize></game>"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createGame_returns415_whenRequestBodyIsMessagePack() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType("application/msgpack")
                        .content(new byte[]{(byte) 0x81, (byte) 0xa9, 'b', 'o', 'a', 'r', 'd', 'S', 'i', 'z', 'e', 10}))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void listGames_returns406_whenClientRequestsMessagePack() throws Exception {
        mockMvc.perform(get("/api/games")
                        .accept("application/msgpack"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void attack_invalidPosition_returns400() throws Exception {
        when(gameService.attack(any(), any())).thenThrow(new IllegalArgumentException("Position already attacked"));

        mockMvc.perform(post("/api/games/1/attacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":0,\"y\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelGame_returns204() throws Exception {
        doNothing().when(gameService).cancelGame(1L);

        mockMvc.perform(delete("/api/games/1"))
                .andExpect(status().isNoContent());
    }
}
