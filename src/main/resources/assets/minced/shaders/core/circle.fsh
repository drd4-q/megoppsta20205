#version 150

in vec2 FragCoord;
in vec4 FragColor;

uniform vec2 Size;
uniform float Radius;
uniform float Smoothness;

out vec4 OutColor;

void main() {
    vec2 center = Size * 0.5;
    vec2 pos = FragCoord * Size;
    float dist = length(pos - center) - Radius;
    float alpha = 1.0 - smoothstep(0.0, Smoothness, dist);
    
    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha);

    if (color.a == 0.0) {
        discard;
    }

    OutColor = color;
} 