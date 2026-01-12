package ru.yandex.practicum.feign.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) return new NotFoundException("Error 404 (NotFound) for the method: " + methodKey);

        if (response.status() == 500) return new InternalServerErrorException("Error 500 (EXCEPTION) at the server");

        return defaultDecoder.decode(methodKey, response);
    }
}