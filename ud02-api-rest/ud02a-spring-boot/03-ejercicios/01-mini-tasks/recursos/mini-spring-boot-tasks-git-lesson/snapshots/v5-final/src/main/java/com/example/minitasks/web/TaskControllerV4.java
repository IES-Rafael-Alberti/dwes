package com.example.minitasks.web;
import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.dto.UpdateTaskDTO;
import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI; import java.time.Duration; import java.util.List;
@RestController @RequestMapping("/v4/tasks")
public class TaskControllerV4 {
  private final TaskService service; public TaskControllerV4(TaskService service){this.service=service;}
  @GetMapping(params = {"!page", "!size"})
  public ResponseEntity<List<Task>> list(@RequestParam(required=false) Boolean done, @RequestHeader(value="If-None-Match", required=false) String ifNoneMatch){
    List<Task> tasks=service.list(done); String etag="\"tasks-"+tasks.size()+"\""; if(etag.equals(ifNoneMatch)){ return ResponseEntity.status(304).eTag(etag).build(); }
    return ResponseEntity.ok().eTag(etag).cacheControl(CacheControl.maxAge(Duration.ofSeconds(30)).cachePublic()).body(tasks);
  }
  @GetMapping(params={"page","size"})
  public ResponseEntity<List<Task>> listPage(@RequestParam(required=false) Boolean done, @RequestParam(required=false) String q, Pageable pageable){
    Page<Task> page=service.listPage(done,q,pageable); String link=buildLinkHeader(page,q,done);
    return ResponseEntity.ok().header("X-Total-Count", String.valueOf(page.getTotalElements())).header(org.springframework.http.HttpHeaders.LINK, link).body(page.getContent());
  }
  @PostMapping public ResponseEntity<Task> create(@RequestBody @jakarta.validation.Valid CreateTaskDTO dto){ Task saved=service.create(dto); return ResponseEntity.created(URI.create("/v4/tasks/"+saved.getId())).body(saved); }
  @PatchMapping("/{id}/toggle") public ResponseEntity<Task> toggle(@PathVariable Long id){ return ResponseEntity.ok(service.toggle(id)); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
  @PutMapping("/{id}") public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody UpdateTaskDTO dto){ return ResponseEntity.ok(service.update(id,dto)); }
  @RequestMapping(method=RequestMethod.HEAD) public ResponseEntity<Void> head(@RequestParam(required=false) Boolean done, @RequestParam(required=false) String q, Pageable pageable){
    Page<Task> page=service.listPage(done,q,pageable); String link=buildLinkHeader(page,q,done);
    return ResponseEntity.ok().header("X-Total-Count", String.valueOf(page.getTotalElements())).header(org.springframework.http.HttpHeaders.LINK, link).build();
  }
  private String buildLinkHeader(Page<?> page, String q, Boolean done){
    StringBuilder sb=new StringBuilder(); String base=ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString(); int number=page.getNumber(); int size=page.getSize();
    if(page.hasNext()){ String next=base+"?page="+(number+1)+"&size="+size+buildExtra(q,done); sb.append('<').append(next).append('>').append("; rel="next""); }
    if(page.hasPrevious()){ if(sb.length()>0) sb.append(", "); String prev=base+"?page="+(number-1)+"&size="+size+buildExtra(q,done); sb.append('<').append(prev).append('>').append("; rel="prev""); }
    if(page.getTotalPages()>0){ if(sb.length()>0) sb.append(", "); String first=base+"?page=0&size="+size+buildExtra(q,done); sb.append('<').append(first).append('>').append("; rel="first"");
      if(sb.length()>0) sb.append(", "); int lastIdx=Math.max(page.getTotalPages()-1,0); String last=base+"?page="+lastIdx+"&size="+size+buildExtra(q,done); sb.append('<').append(last).append('>').append("; rel="last""); }
    return sb.toString();
  }
  private String buildExtra(String q, Boolean done){
    StringBuilder extra=new StringBuilder(); if(q!=null && !q.isBlank()) extra.append("&q=").append(q); if(done!=null) extra.append("&done=").append(done); return extra.toString();
  }
}