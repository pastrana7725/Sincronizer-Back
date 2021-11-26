/**  
 * @Title:  APIService.java   
 * @Package co.edu.usbcali.sincronizer.service   
 * @Description: description   
 * @author: Diego Pastrana     
 * @date:   2/11/2021 5:15:20 p. m.   
 * @version V1.0 
 * @Copyright: Universidad San de Buenaventura
 */
package co.edu.usbcali.sincronizer.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.usbcali.sincronizer.domain.Employees;
import co.edu.usbcali.sincronizer.domain.Logs;
import co.edu.usbcali.sincronizer.domain.Projects;
import co.edu.usbcali.sincronizer.domain.Task;
import co.edu.usbcali.sincronizer.domain.TaskAssignment;
import co.edu.usbcali.sincronizer.dto.EmployeesDTO;
import co.edu.usbcali.sincronizer.dto.ProjectsDTO;
import co.edu.usbcali.sincronizer.dto.TaskAssignmentDTO;
import co.edu.usbcali.sincronizer.dto.TaskDTO;
import co.edu.usbcali.sincronizer.repository.TaskAssignmentRepository;

/**   
 * @ClassName:  APIService   
  * @Description: TODO   
 * @author: Diego Pastrana     
 * @date:   2/11/2021 5:15:20 p. m.      
 * @Copyright:  USB
 */

@Scope("singleton")
@Service
public class APIService {
	
	@Autowired
	private ProjectsService projectsService;
	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskAssignmentRepository taskAssignmentRepository;
	@Autowired
	private TaskAssignmentService taskAssignmentService;
	
	
	//ESTE MÉTODO OBTIENE LOS PLANES DEL API DE PLANNER CON UN GROUP ID
	public JSONArray GETPlans(String groupIdParam, String tokenParam) throws Exception{
		RestTemplate template = new RestTemplate();
		String groupId=groupIdParam;
		JSONArray jsonArray=null;
		//EL ACCESSTOKEN VARÍA CADA CIERTO TIEMPO
		String accessToken=tokenParam;
		//CONSTRUIMOS NUESTRO URL
		UriComponents uri = UriComponentsBuilder.newInstance()
		.scheme("https")
		.host("graph.microsoft.com")
		.path("v1.0/groups/"+groupId+"/planner/plans")
		//.queryParam("Authorization",accessToken) //ESTA LINEA SE BORRA SI QUISIERAMOS AGREGAR PARÁMETROS (PUEDEN SER VARIAS QUERYPARAM)
		.build();
		
		//AGREGAMOS EL TOKEN DENTRO DE LOS HEADERS DE LA PETICIÓN
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);
		
		/*CREAMOS LA ENTIDAD, DONDE ESTÁ NULL VAN PARÁMETROS, PERO NO ES NECESARIO
		 PORQUE LOS PARÁMETROS LOS ESTAMOS PASANDO EN LA CONSTRUCCIÓN DEL URL.
		 SE LE PASAN DE SEGUNDO LOS HEADERS QUE CREAMOS ANTERIORMENTE*/
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		
		try {
		//REALIZAMOS LA PETICIÓN AGREGANDOLE EL URL, EL MÉTODO (GET,POST,UPDATE,DELETE, ETC), LA ENTIDAD
		//QUE CREAMOS ANTERIORMENTE Y FINALMENTE LA INTERFAZ DE NUESTRA RESPUESTA, EN ESTE CASO STRING
		ResponseEntity<String> respEntity=template.exchange(uri.toUriString(), HttpMethod.GET, entity, String.class);
		
		//CONVERTIMOS LA RESPUESTA STRING A UN OBJETO JSON PARA SER MÁS FÁCIL EL MANEJO DE LA INFORMACIÓN
		JSONObject json = new JSONObject(respEntity.getBody());
		
		//CREAMOS UN ARREGLO DE JSON, DADO QUE PUEDEN SER VARIAS RESPUESTAS DE LA PETICIÓN
		jsonArray= new JSONArray(json.get("value").toString());
		
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return jsonArray;
	}
	//ESTE MÉTODO OBTIENE UN USUARIO DEL API DE PLANNER CON UN USER ID
	public JSONObject GETUser(String idUser, String token) throws Exception { 
		RestTemplate template = new RestTemplate();
		String userId=idUser;
		JSONObject json=null;
		//EL ACCESSTOKEN VARÍA CADA CIERTO TIEMPO
		String accessToken=token;
		//CONSTRUIMOS NUESTRO URL
		UriComponents uri = UriComponentsBuilder.newInstance()
		.scheme("https")
		.host("graph.microsoft.com")
		.path("v1.0/users/"+userId)
		//.queryParam("Authorization",accessToken) //ESTA LINEA SE BORRA SI QUISIERAMOS AGREGAR PARÁMETROS (PUEDEN SER VARIAS QUERYPARAM)
		.build();
		
		//AGREGAMOS EL TOKEN DENTRO DE LOS HEADERS DE LA PETICIÓN
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);
		
		/*CREAMOS LA ENTIDAD, DONDE ESTÁ NULL VAN PARÁMETROS, PERO NO ES NECESARIO
		 PORQUE LOS PARÁMETROS LOS ESTAMOS PASANDO EN LA CONSTRUCCIÓN DEL URL.
		 SE LE PASAN DE SEGUNDO LOS HEADERS QUE CREAMOS ANTERIORMENTE*/
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		
		try {
		//REALIZAMOS LA PETICIÓN AGREGANDOLE EL URL, EL MÉTODO (GET,POST,UPDATE,DELETE, ETC), LA ENTIDAD
		//QUE CREAMOS ANTERIORMENTE Y FINALMENTE LA INTERFAZ DE NUESTRA RESPUESTA, EN ESTE CASO STRING
		ResponseEntity<String> respEntity=template.exchange(uri.toUriString(), HttpMethod.GET, entity, String.class);
		
		//CONVERTIMOS LA RESPUESTA STRING A UN OBJETO JSON PARA SER MÁS FÁCIL EL MANEJO DE LA INFORMACIÓN
		json = new JSONObject(respEntity.getBody());
		
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	//ESTE MÉTODO OBTIENE LAS TAREAS DEL API DE PLANNER CON UN PLAN ID
	public JSONArray GETTasks(String planIdParam, String tokenParam) {
		RestTemplate template = new RestTemplate();
		String planId=planIdParam;
		JSONArray jsonArray=null;
		//EL ACCESSTOKEN VARÍA CADA CIERTO TIEMPO
		String accessToken=tokenParam;
		
		//CONSTRUIMOS NUESTRO URL
		UriComponents uri = UriComponentsBuilder.newInstance()
		.scheme("https")
		.host("graph.microsoft.com")
		.path("v1.0/planner/plans/"+planId+"/tasks?assignments")
		//.queryParam("Authorization",accessToken) //ESTA LINEA SE BORRA SI QUISIERAMOS AGREGAR PARÁMETROS (PUEDEN SER VARIAS QUERYPARAM)
		.build();
		
		//AGREGAMOS EL TOKEN DENTRO DE LOS HEADERS DE LA PETICIÓN
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);
		
		/*CREAMOS LA ENTIDAD, DONDE ESTÁ NULL VAN PARÁMETROS, PERO NO ES NECESARIO
		 PORQUE LOS PARÁMETROS LOS ESTAMOS PASANDO EN LA CONSTRUCCIÓN DEL URL.
		 SE LE PASAN DE SEGUNDO LOS HEADERS QUE CREAMOS ANTERIORMENTE*/
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		
		try {
		//REALIZAMOS LA PETICIÓN AGREGANDOLE EL URL, EL MÉTODO (GET,POST,UPDATE,DELETE, ETC), LA ENTIDAD
		//QUE CREAMOS ANTERIORMENTE Y FINALMENTE LA INTERFAZ DE NUESTRA RESPUESTA, EN ESTE CASO STRING
		ResponseEntity<String> respEntity=template.exchange(uri.toUriString(), HttpMethod.GET, entity, String.class);
		
		//CONVERTIMOS LA RESPUESTA STRING A UN OBJETO JSON PARA SER MÁS FÁCIL EL MANEJO DE LA INFORMACIÓN
		JSONObject json = new JSONObject(respEntity.getBody());
		
		//CREAMOS UN ARREGLO DE JSON, DADO QUE PUEDEN SER VARIAS RESPUESTAS DE LA PETICIÓN
		jsonArray= new JSONArray(json.get("value").toString());
		
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return jsonArray;
	}
	//ESTE MÉTODO OBTIENE LOS DETALLES DE UNA TAREA
	public JSONObject GETTaskDetail(String taskIdParam, String tokenParam) {
		RestTemplate template = new RestTemplate();
		String taskId=taskIdParam;
		JSONObject json=null;
		//EL ACCESSTOKEN VARÍA CADA CIERTO TIEMPO
		String accessToken=tokenParam;
		
		//CONSTRUIMOS NUESTRO URL
		UriComponents uri = UriComponentsBuilder.newInstance()
		.scheme("https")
		.host("graph.microsoft.com")
		.path("v1.0/planner/tasks/"+taskId+"/details")
		//.queryParam("Authorization",accessToken) //ESTA LINEA SE BORRA SI QUISIERAMOS AGREGAR PARÁMETROS (PUEDEN SER VARIAS QUERYPARAM)
		.build();
		
		//AGREGAMOS EL TOKEN DENTRO DE LOS HEADERS DE LA PETICIÓN
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);
		
		/*CREAMOS LA ENTIDAD, DONDE ESTÁ NULL VAN PARÁMETROS, PERO NO ES NECESARIO
		 PORQUE LOS PARÁMETROS LOS ESTAMOS PASANDO EN LA CONSTRUCCIÓN DEL URL.
		 SE LE PASAN DE SEGUNDO LOS HEADERS QUE CREAMOS ANTERIORMENTE*/
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		
		try {
		//REALIZAMOS LA PETICIÓN AGREGANDOLE EL URL, EL MÉTODO (GET,POST,UPDATE,DELETE, ETC), LA ENTIDAD
		//QUE CREAMOS ANTERIORMENTE Y FINALMENTE LA INTERFAZ DE NUESTRA RESPUESTA, EN ESTE CASO STRING
		ResponseEntity<String> respEntity=template.exchange(uri.toUriString(), HttpMethod.GET, entity, String.class);
		//CONVERTIMOS LA RESPUESTA STRING A UN OBJETO JSON PARA SER MÁS FÁCIL EL MANEJO DE LA INFORMACIÓN
		json = new JSONObject(respEntity.getBody());
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	//ESTE MÉTODO GUARDA LOS PLANES Y EL USUARIO CREADOR DEL PLAN
	public void savePlans (String groupId, String token) throws Exception{
		
		ProjectsDTO projectsDTO=new ProjectsDTO();
		EmployeesDTO employeesDTO=new EmployeesDTO();
		List<ProjectsDTO> lstProjectsDTO=new ArrayList<>();
		
		try {
			/*//ELIMINAMOS LOS PROYECTOS EXISTENTES EN LA BASE DE DATOS
			List<Projects> lstProjects= projectsService.findByStatus("A");
			
			for(int idx=0;idx<lstProjects.size();idx++) {
				projectsService.eliminarProjects(lstProjects.get(idx).getId());
			}*/
			
			//OBTENEMOS LOS PLANES EN FORMATO JSON ARRAY MEDIANTE EL MÉTODO GETPlans
			JSONArray plans=GETPlans(groupId,token);
			
			if (plans!=null) {
				//RECORREMOS EL ARRAY PLAN POR PLAN
				for(int idx=0; idx<plans.length();idx++ ) {
					
					JSONObject object= plans.getJSONObject(idx);
					
					//MAPEAMOS EL PROYECTO CON LOS PARÁMETROS PROVENIENTES DEL JSON
					projectsDTO.setId(object.getString("id"));
					projectsDTO.setName(object.getString("title"));
					projectsDTO.setStatus("A");
					
					//DADO QUE EL JSON LA FECHA VIENE EN FORMATO STRING, LA PARSEAMOS HACIA TIPO DE DATO DATE
					String fecha=object.getString("createdDateTime");
					fecha=fecha.substring(0,9);
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-mm-dd"); 
					Date date=formato.parse(fecha);
					
					projectsDTO.setCreationDate(date);
					projectsDTO.setOwner(object.getString("owner"));
					
					//OBTENEMOS EL ID DEL USUARIO CREADOR DEL PLAN
					JSONObject createdBy= object.getJSONObject("createdBy");
					JSONObject user = createdBy.getJSONObject("user");
					String userId = user.get("id").toString();
					
					//CONSULTAMOS EL USUARIO MEDIANTE SU ID CON LA CONSULTA DEL MÉTODO GETUser
					user=GETUser(userId,token);
					//MAPEAMOS EL EMPLOYEE CON LOS DATOS PROVENIENTES DE LA CONSULTA EN FORMATO JSON
					employeesDTO.setId(user.getString("id"));
					employeesDTO.setName(user.getString("givenName"));
					employeesDTO.setLastName(user.getString("surname"));
					employeesDTO.setEmail(user.getString("mail"));
					employeesDTO.setStatus("A");
					
					Employees employee= null;
					//CONSULTAMOS SI EL USUARIO EXISTE, SI EXISTE LO ACTUALIZAMOS, SINO LO CREAMOS
					employee=employeesService.findById(userId);
					if(employee==null) {
						employeesService.guardarEmpleado(employeesDTO);
					}
					else {
						employeesService.actualizarEmpleado(employeesDTO);
					}
					
					projectsDTO.setEmployeeId(userId);
					
					//CONSULTAMOS SI EL PLAN EXISTE, SI EXISTE LO ACTUALIZAMOS, SINO LO CREAMOS
					Projects projects=null;
					projects=projectsService.findById(projectsDTO.getId());
					if(projects==null) {
						projectsService.guardarProject(projectsDTO);
						lstProjectsDTO.add(projectsDTO);
					}
					else {
						projectsService.actualizarProject(projectsDTO);
						lstProjectsDTO.add(projectsDTO);
					}
					
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	//ESTE MÉTODO GUARDA LAS TAREAS 
	public void saveTasks (String planId, String token) throws Exception{
		
		TaskDTO taskDTO = new TaskDTO();
				
		try {
			//OBTENEMOS UN ARRAY DE JSONS QUE CONTIENEN TODAS LAS TAREAS DE UN PLAN ESPECÍFICO
			JSONArray tasks=GETTasks(planId,token);
			
			if(tasks!=null) {
			
				//RECORREMOS EL ARRAY TASK POR TASK
				for(int idx=0;idx<tasks.length();idx++) {
					
					JSONObject object=tasks.getJSONObject(idx);
					//MAPEAMOS LA TAREA CON LOS ATRIBUTOS DEL JSON DE LA CONSULTA
					taskDTO.setId(object.getString("id"));
					
					//OBTENEMOS LOS DETALLES DE LA TAREA
					JSONObject taskDetail = GETTaskDetail(taskDTO.getId(), token);
					taskDTO.setNotes(taskDetail.getString("description"));
					taskDTO.setName(object.getString("title"));
					taskDTO.setComments("SOLUCIONAR ESTO DESPUÉS");
					
					//DADO QUE EL JSON LA FECHA VIENE EN FORMATO STRING, LA PARSEAMOS HACIA TIPO DE DATO DATE
					String fecha1=object.getString("startDateTime");
					fecha1=fecha1.substring(0,9);
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-mm-dd"); 
					Date date1=formato.parse(fecha1);
					taskDTO.setStartDate(date1);
					//DADO QUE EL JSON LA FECHA VIENE EN FORMATO STRING, LA PARSEAMOS HACIA TIPO DE DATO DATE
					String fecha2=object.getString("dueDateTime");
					fecha2=fecha2.substring(0,9);
					Date date2=formato.parse(fecha2);
					taskDTO.setDueDate(date2);
					
					JSONObject checklists= taskDetail.getJSONObject("checklist");
					List<String> cleanChecklists= new ArrayList<>();
					if(!checklists.toString().equals("{}")) {
						for(int i=0;i<checklists.names().length();i++) {
							cleanChecklists.add(checklists.get(checklists.names().get(i).toString()).toString());
						}
						JSONObject checklist = new JSONObject(cleanChecklists.get(cleanChecklists.size()-1));
						taskDTO.setChecklist(checklist.getString("title"));
					}
					else {
						taskDTO.setChecklist("");
					}
					

					//NO VIENE
					taskDTO.setPriorityId(2L);
					
					taskDTO.setProjectId(object.getString("planId"));
					//EL STATUS ES ES NO INICIADA CUANDO EL percentComplete=0
					//EL STATUS ESTA EN PROGRESO CUANDO EL percentComplete=50
					//EL STATUS ESTA COMPLETADA CUANDO EL percentComplete=100
					long id=0;
					if(object.get("percentComplete").toString()=="0") {
						id=0;
					}
					else if(object.get("percentComplete").toString()=="50") {
						id=1;
					}
					else {
						id=2;
					}
					taskDTO.setStatusId(id);
					
					//CONSULTAMOS SI YA EXISTE LA TAREA, SI NO EXISTE LA CREAMOS, SI SÍ EXISTE LA ACTUALIZA
					Task task=null;
					task=taskService.findById(taskDTO.getId());
					if(task==null) {
						taskService.guardarTask(taskDTO);
					}
					else {
						taskService.actualizarTask(taskDTO);
					}
					
					saveUsersAndAssignments(object, token);
				}
			}
		}	
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	//ESTE MÉTODO GUARDA LOS USUARIOS ASIGNADOS A UNA TAREA Y LAS ASIGNACIONES A ESA TAREA
	public void saveUsersAndAssignments(JSONObject object, String token)throws Exception{
		
		//GUARDAMOS LOS USUARIOS A LOS QUE SE LES ASIGNÓ LA TAREA
		//DE LA TAREA OBTENEMOS EL APARTADO DE ASIGNACIONES
		JSONObject assignments = object.getJSONObject("assignments");
		//SI SÍ HAY ASIGNACIONES, ENTONCES:
		if(!assignments.toString().equals("{}")) {
			//RECORREMOS EL JSON POR SUS KEYS
			for(int i=0;i<assignments.names().length();i++) {
				String userId="";
				JSONObject user;
				EmployeesDTO employeeDTO = new EmployeesDTO();
				TaskAssignmentDTO taskAssignmentDTO = new TaskAssignmentDTO();
				
				//ESCARBAMOS HASTA APUNTAR HACIA EL ID DEL USUARIO
				userId=assignments.names().get(i).toString();
				//OBTENEMOS EL USUARIO POR SU ID
				user=GETUser(userId, token);
				
				//MAPEAMOS EL NUEVO EMPLEADO
				employeeDTO.setId(user.getString("id"));
				employeeDTO.setEmail(user.getString("mail"));
				employeeDTO.setName(user.getString("givenName"));
				employeeDTO.setLastName(user.getString("surname"));
				employeeDTO.setStatus("A");
				
				//PREGUNTAMOS SI ESE EMPLEADO EXISTE PARA GUARDARLO O ACTUALIZARLO
				Employees employee=null;
				employee=employeesService.findById(userId);
				if(employee==null) {
					employeesService.guardarEmpleado(employeeDTO);
				}
				else {
					employeesService.actualizarEmpleado(employeeDTO);
				}
				
				//MAPEAMOS LA NUEVA ASIGNACIÓN
				taskAssignmentDTO.setTaskId(object.getString("id"));
				taskAssignmentDTO.setEmployeeId(userId);
				taskAssignmentDTO.setRolId(2);
				taskAssignmentService.guardarTaskAssignment(taskAssignmentDTO);
				
			}
		}
	}
	//ESTE MÉTODO GUARDA EL LOG Y EL TASKLOG
	public void saveLogAndTaskLog(JSONObject object, String token)throws Exception{
		
		LocalDate todaysDate = LocalDate.now();
		
		Logs log = new Logs();
		log.setCreationDate(todaysDate);
		//log.setEmployeeRegistrador();
		
	}
	

}
