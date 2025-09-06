#version 150

in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform float Range; // distance field range of the msdf font texture
uniform float Thickness; // text thickness
uniform float Smoothness; // edge smoothness
uniform bool Outline; // if false, outline computation will be ignored
uniform float OutlineThickness;
uniform vec4 OutlineColor;

out vec4 OutColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    vec3 msdf = texture(Sampler0, TexCoord).rgb;
    float dist = median(msdf) - 0.5 + Thickness;

    vec2 h = vec2(dFdx(TexCoord.x), dFdy(TexCoord.y)) * textureSize(Sampler0, 0);
    float scale = Range * inversesqrt(dot(h, h) / 2.0);

    float safeSmoothness = max(0.001, Smoothness);
    float alpha = smoothstep(-safeSmoothness, safeSmoothness, dist * scale);

    if (alpha < 0.001) {
        discard;
    }
    
    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha);
    
    if (Outline) {
        float outlineDist = dist + OutlineThickness;
        float outlineAlpha = smoothstep(-safeSmoothness, safeSmoothness, outlineDist * scale);

        if (outlineAlpha < 0.001) {
            discard;
        }

        color = mix(vec4(OutlineColor.rgb, OutlineColor.a * outlineAlpha), 
                   vec4(FragColor.rgb, FragColor.a * alpha), 
                   clamp(alpha / max(0.001, outlineAlpha), 0.0, 1.0));
    }
    
    OutColor = color;
}