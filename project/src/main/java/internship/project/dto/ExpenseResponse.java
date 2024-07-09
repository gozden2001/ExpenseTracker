package internship.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExpenseResponse {
    private List<ExpenseDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private String sortedBy;
}
