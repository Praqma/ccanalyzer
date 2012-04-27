package net.praqma.ccanalyzer;

import java.util.List;

import net.praqma.util.PackageUtils;
import net.praqma.util.option.CLI;

public class Main {
	public static void main( String[] args ) throws Exception {
		
		if( args.length > 0 ) {
			/* Fist argument is sub program */
			String app = args[0];
			
			Class<CLI> clazz = (Class<CLI>) Class.forName( "net.praqma.ccanalyzer." + app );
			CLI cli = clazz.newInstance();
			cli.perform( args );
		} else {
			List<Class<?>> list = PackageUtils.getClasses( "net.praqma.ccanalyzer", null );
			
			System.out.println( "Possible sub programs:" );
			for( Class<?> c : list ) {
				if( CLI.class.isAssignableFrom( c ) && !c.equals( CLI.class ) ) {
					Class<? extends CLI> c1 = (Class<? extends CLI>) c;
					System.out.println( " * " + c.getSimpleName() );
				}
			}
			
			
		}
	}
}

