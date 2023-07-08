package juuxel.bloom.backend.response;

import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.RecordCodecs;
import juuxel.bloom.backend.util.Fn;

public record ClassContentResponse(String className, String content) implements Response {
    public static final String TYPE = "class_content";
    public static final Codec<ClassContentResponse> CODEC = RecordCodecs.<ClassContentResponse>builder()
        .field("class_name", Codec.STRING, ClassContentResponse::className)
        .field("content", Codec.STRING, ClassContentResponse::content)
        .build(Fn.reverse(ClassContentResponse::new));

    @Override
    public String getType() {
        return TYPE;
    }
}
