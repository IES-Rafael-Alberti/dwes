package com.example.battleship.config;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Writes the existing JSON object model as MessagePack when a client requests
 * {@code application/msgpack}. Reading remains deliberately out of scope for
 * this optional content-negotiation case study.
 */
public final class MessagePackHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final MediaType MESSAGE_PACK = MediaType.parseMediaType("application/msgpack");

    private final ObjectMapper objectMapper;

    public MessagePackHttpMessageConverter(ObjectMapper objectMapper) {
        super(MESSAGE_PACK);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException("MessagePack request bodies are not supported in this case study");
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException {
        try (MessagePacker packer = MessagePack.newDefaultPacker(outputMessage.getBody())) {
            pack(objectMapper.valueToTree(object), packer);
        }
    }

    private void pack(JsonNode node, MessagePacker packer) throws IOException {
        if (node.isNull()) {
            packer.packNil();
        } else if (node.isObject()) {
            var properties = node.properties();
            packer.packMapHeader(properties.size());
            for (var entry : properties) {
                packer.packString(entry.getKey());
                pack(entry.getValue(), packer);
            }
        } else if (node.isArray()) {
            packer.packArrayHeader(node.size());
            for (JsonNode child : node) {
                pack(child, packer);
            }
        } else if (node.isBoolean()) {
            packer.packBoolean(node.booleanValue());
        } else if (node.isIntegralNumber()) {
            packer.packLong(node.longValue());
        } else if (node.isNumber()) {
            packer.packDouble(node.doubleValue());
        } else {
            packer.packString(node.asString());
        }
    }
}
