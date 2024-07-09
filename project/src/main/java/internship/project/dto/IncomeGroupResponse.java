package internship.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class IncomeGroupResponse {
    private List<IncomeGroupDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private String sortedBy;
}
