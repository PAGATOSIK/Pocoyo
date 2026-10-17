package main.client.render;

@FunctionalInterface
public interface VertexSink {
    /**
     * Добавляет вершину с координатами и цветом (ARGB) в буфер рендеринга.
     *
     * @param x Координата X
     * @param y Координата Y
     * @param z Координата Z
     * @param red Компонента красного (0.0f - 1.0f или в виде байта, в зависимости от логики конвейера)
     * @param green Компонента зеленого
     * @param blue Компонента синего
     * @param alpha Прозрачность (альфа)
     */
    void vertex(float x, float y, float z, float red, float green, float blue, float alpha);
}