package it.osys.jaxrsodata.filter;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.osys.jaxrsodata.antlr4.ODataFilterParser;
import it.osys.jaxrsodata.antlr4.ODataFilterParser.ExprContext;

public class DefaultJPAFilterVisitor<T> implements JPAFilterVisitor<T> {

	private Root<T> root;
	private CriteriaBuilder cb;
	private EntityManager em;

	@Override
	public void setRoot(Root<T> root) {
		this.root = root;
	}

	@Override
	public void setCb(CriteriaBuilder cb) {
		this.cb = cb;
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public Object visit(ExprContext context) {

		if (context.AND() != null)
			return cb.and((Predicate) visit(context.expr(0)), (Predicate) visit(context.expr(1)));

		if (context.OR() != null)
			return cb.or((Predicate) visit(context.expr(0)), (Predicate) visit(context.expr(1)));

		if (context.NOT() != null)
			return cb.not((Predicate) visit(context.expr(0)));

		if (context.start.getType() == ODataFilterParser.BR_OPEN)
			return visit(context.expr(0));

		DefaultJPAFilterDao<T> filterDao = new DefaultJPAFilterDao<T>();
		filterDao.setCb(cb);
		filterDao.setRoot(root);
		filterDao.setEm(em);
		filterDao.setup(context);

		Predicate predicate = filterDao.getPredicate();
		if (predicate != null)
			return predicate;

		throw new IllegalStateException();
	}

}
