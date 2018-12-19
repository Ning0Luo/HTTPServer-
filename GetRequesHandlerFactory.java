import java.lang.reflect.GenericArrayType;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ningluo
 */
public class GetRequesHandlerFactory implements ISocketReadWriteHandlerFactory {
    private HashMap<String, String> NameMap;
    
    public GetRequesHandlerFactory(HashMap<String,String> hm) {
		NameMap  = hm;
	}
    
	@Override
	public IReadWriteHandler createHandler() {
		GetRequestHandler rwh = new GetRequestHandler();
		rwh.SetNameMap(NameMap);
		return rwh;
	}
	
    
	
}
