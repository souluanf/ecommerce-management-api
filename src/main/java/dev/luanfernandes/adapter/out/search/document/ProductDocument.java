package dev.luanfernandes.adapter.out.search.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/product-settings.json")
public record ProductDocument(
        @Id String id,
        @Field(type = FieldType.Text, analyzer = "multilingual_search", searchAnalyzer = "multilingual_search")
                String name,
        @Field(type = FieldType.Text, analyzer = "multilingual_search", searchAnalyzer = "multilingual_search")
                String description,
        @Field(type = FieldType.Scaled_Float, scalingFactor = 100) BigDecimal price,
        @Field(type = FieldType.Keyword) String category,
        @Field(type = FieldType.Integer) Integer stockQuantity,
        @Field(
                        type = FieldType.Date,
                        format = {},
                        pattern = "yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime createdAt,
        @Field(
                        type = FieldType.Date,
                        format = {},
                        pattern = "yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime updatedAt,
        @Field(type = FieldType.Boolean) Boolean available) {
    public static ProductDocument from(
            String id,
            String name,
            String description,
            BigDecimal price,
            String category,
            Integer stockQuantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new ProductDocument(
                id,
                name,
                description,
                price,
                category,
                stockQuantity,
                createdAt,
                updatedAt,
                stockQuantity != null && stockQuantity > 0);
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
}
