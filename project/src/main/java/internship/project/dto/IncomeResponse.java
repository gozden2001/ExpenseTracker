package internship.project.dto;

import internship.project.model.Income;
import lombok.Data;

import java.util.List;

@Data
public class IncomeResponse {
    private List<IncomeDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private String sortedBy;
}
