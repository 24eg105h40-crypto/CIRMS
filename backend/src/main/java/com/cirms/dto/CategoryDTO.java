package com.cirms.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private Long itemCount;

    public CategoryDTO() {}

    public CategoryDTO(Long id, String name, String description, Long itemCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.itemCount = itemCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Long itemCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder itemCount(Long itemCount) { this.itemCount = itemCount; return this; }

        public CategoryDTO build() {
            return new CategoryDTO(id, name, description, itemCount);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getItemCount() { return itemCount; }
    public void setItemCount(Long itemCount) { this.itemCount = itemCount; }
}
