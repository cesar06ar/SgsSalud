/**
* This file is part of Glue: Adhesive BRMS
*
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías Cia Ltda)
* Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
*
* If you are developing and distributing open source applications under
* the GNU General Public License (GPL), then you are free to re-distribute Glue
* under the terms of the GPL, as follows:
*
* GLue is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Glue is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Glue. If not, see <http://www.gnu.org/licenses/>.
*
* For individuals or entities who wish to use Glue privately, or
* internally, the following terms do not apply:
*
* For OEMs, ISVs, and VARs who wish to distribute Glue with their
* products, or host their product online, Eqaula provides flexible
* OEM commercial licenses.
*
* Optionally, Customers may choose a Commercial License. For additional
* details, contact an Eqaula representative (sales@eqaula.org)
*/
package edu.sgssalud.util;

import java.util.List;
import java.util.Map;

public class QueryData<E> {

	private List<E> result;
	private Long totalResultCount;

	private int start;
	private int end;
	private String sortField;
	private QuerySortOrder order;
	private Map<String, String> filters;

	public QueryData() {
		this(0, 0, "", QuerySortOrder.ASC, null);
	}

	public QueryData(int start, int end, String field, QuerySortOrder order,
			Map<String, String> filters) {

		this.start = start;
		this.end = end;
		this.sortField = field;
		this.order = order;
		this.filters = filters;
	}

	public List<E> getResult() {
		return result;
	}

	public Long getTotalResultCount() {
		return totalResultCount;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public QuerySortOrder getOrder() {
		return order;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setResult(List<E> result) {
		this.result = result;
	}

	public void setTotalResultCount(Long totalResultCount) {
		this.totalResultCount = totalResultCount;
	}

	public String getSortField() {
		return sortField;
	}

	@Override
	public String toString() {
		return "QueryData [start=" + start + ", end=" + end
				+ ", sortField=" + sortField + ", order=" + order
				+ ", filters=" + filters + "]";
	}

}