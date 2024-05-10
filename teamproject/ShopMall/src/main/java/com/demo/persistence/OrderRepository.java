package com.demo.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.demo.domain.OrderDetail;
import com.demo.domain.Orders;
import com.demo.dto.SalesCountInterface;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
	
	//다음 주문번호 생성
	@Query(value="select NVL2(max(oseq),max(oseq)+1,1) from orders", nativeQuery=true)
	public int selectMaxOseq();
	
	//사용자별 상세 주문내역 조회(JPQL 사용 - domain 클래스명 사용)
	@Query(" SELECT od FROM OrderDetail od " + 
			"JOIN Orders o ON od.order.oseq =o.oseq "
			+ "JOIN Product p ON od.product.pseq = p.pseq "
			+ "JOIN Member m ON o.member.id=m.id "
			+ "WHERE o.member.id=?1 "
			+ "AND od.order.oseq=?2 "
			+ "AND od.result LIKE %?3% ")
	public List<OrderDetail> getListOrderById(String id , int oseq, String result);

	// 사용자별 주문내역 조회
	@Query("SELECT order FROM Orders order "
			+ "JOIN Member m ON order.member.id=m.id "
			+ "WHERE order.member.id=?1 AND order.oseq=?2")
	public Orders getOrderByMemberId(String id, int oseq);
	
	//사용자별 전체 주문번호 조회 (JPQL)
	@Query("SELECT DISTINCT od.order.oseq "
			+ "FROM OrderDetail od JOIN Orders o ON od.order.oseq=o.oseq "
			+ "JOIN Member m ON o.member.id=m.id "
			+ "WHERE o.member.id=?1 AND od.result LIKE %?2% "
			+ "ORDER BY od.order.oseq DESC ")
	public List<Integer> getSeqOrdering(String id, String result);
	
	//주문목록 출력
	@Query("SELECT od FROM OrderDetail od "
			+ "WHERE od.order.member.name LIKE %?1% "
			+ "ORDER BY od.result, od.order.oseq DESC")
	public List<OrderDetail> getOrderListByName(String name);
	
	//제품별 판매실적 조회
	@Query(value="select name pname, sum(quantity) sales_count "
			   + "  FROM order_view "
			   + " group by name", nativeQuery=true)
	List<SalesCountInterface> findSalesCountReport();
	
}

