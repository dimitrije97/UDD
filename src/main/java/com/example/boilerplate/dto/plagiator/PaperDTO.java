package com.example.boilerplate.dto.plagiator;

import java.util.Comparator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class PaperDTO implements Comparator<PaperDTO>{

	private Long id;
	private String title;
	private MultipartFile file;
	private String pathForPDF;
	
//	private String content;
//	private String text;
	private double searchHits;
	private double similarProcent;
	private UserDTO user;
	private Long plagiatorId;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		PaperDTO other = (PaperDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}
	@Override
	public int compare(PaperDTO paper1, PaperDTO paper2) {
		if(paper1.getSimilarProcent() < paper2.getSimilarProcent()) {
			return 1;
		}
		else {
			return -1;
		}
	}
}





