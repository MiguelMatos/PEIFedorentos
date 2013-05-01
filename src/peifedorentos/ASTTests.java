package peifedorentos;

public class ASTTests {

	private static class StaticClass {
		public static int staticMethod() {
			return 0;
		}
	}
	
	public void m() {
		
		StaticClass.staticMethod();
		
		int i = StaticClass.staticMethod();
		
		int a;
		a = StaticClass.staticMethod();
		
	}
}


