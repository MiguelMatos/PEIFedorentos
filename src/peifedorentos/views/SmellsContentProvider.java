package peifedorentos.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import peifedorentos.smells.ISmell;

public class SmellsContentProvider implements IStructuredContentProvider {

	private static List<ISmell> smells = new ArrayList<ISmell>();
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object arg0) {
		
		return getSmells().toArray();
	}

	public List<ISmell> getSmells() {
		return smells;
	}
	
	public void addSmell(ISmell smell) {
		if (!smells.contains(smell))
			smells.add(smell);
	}
	
	public void addSmells(List<ISmell> smellList)
	{
		if (smells.containsAll(smellList))
			return;			
		else 
		{
			for (ISmell smell : smellList)
				addSmell(smell);
		}
			
	}



}
