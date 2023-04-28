package jfox.context;

public interface IContext {

	<T> T getBean(Class<T> type);

	void addBean(Object bean);

	void setMainContext(ContextAbstract mainContext);
	
	void close();

}