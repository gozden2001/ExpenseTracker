package internship.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExpenseGroupResponse {
    private List<ExpenseGroupDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private String sortedBy;
}
