/**  
 * @Title:  SincronizationController.java   
 * @Package co.edu.usbcali.sincronizer.controller   
 * @Description: description   
 * @author: Diego Pastrana     
 * @date:   21/10/2021 8:17:04 p. m.   
 * @version V1.0 
 * @Copyright: Universidad San de Buenaventura
 */
package co.edu.usbcali.sincronizer.controller;


import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import co.edu.usbcali.sincronizer.domain.Projects;
import co.edu.usbcali.sincronizer.dto.TaskDTO;
import co.edu.usbcali.sincronizer.service.APIService;
import co.edu.usbcali.sincronizer.service.ProjectsService;


/**  
 * @ClassName:  SincronizationController   
  * @Description: TODO   
 * @author: Diego Pastrana     
 * @date:   21/10/2021 8:17:04 p. m.      
 * @Copyright:  USB
 */

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class SincronizationController {
	
	@Autowired
	private APIService apiService;
	@Autowired
	private ProjectsService projectsService;
	
	@PostMapping("/sync")
	private ResponseEntity<?> sincronizar (@RequestParam("groupId") String groupId, @RequestParam("token") String token){
		
		try {
			//OBTENEMOS LOS PLANES Y LOS GUARDAMOS
			apiService.savePlans(groupId,token);
			
			//GUARDAMOS LAS TAREAS DE CADA PLAN
			List <Projects> listProjects= projectsService.findByStatus("A");
			for(Projects project : listProjects) {
				apiService.saveTasks(project.getId(), token);
			}
			
			
			return ResponseEntity.ok().body(null);
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	
	
	

}
