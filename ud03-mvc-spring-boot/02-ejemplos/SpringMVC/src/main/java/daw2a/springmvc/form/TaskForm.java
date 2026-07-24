package daw2a.springmvc.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskForm {
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description = "";
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
