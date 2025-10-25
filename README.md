
  Descripción del proyecto
- Aplicación móvil Android (Java) para registrar "Experiencias" (producto, servicio, costo, categoría, descripción, favorito).
- Sincronización bidireccional con Google Calendar mediante n8n: cuando se crea/edita una experiencia se crea/actualiza un evento en Google Calendar; cuando hay cambios en Calendar, se envían a la app/servicio para actualizar la experiencia.
- Comunicación por webhooks HTTP entre tu backend/servicio en Railway y los workflows de n8n.
- Endpoints públicos en tu despliegue Railway:
  - `https://primary-production-6c81c.up.railway.app/webhook-test/experiencias/webhook`
•	Usa webhooks para comunicación entre tu app y n8n 
•	Maneja errores tanto en la app como en n8n
Resumen final
- Comunicación por webhooks entre app/backend y n8n; n8n actúa como orquestador entre la app y Google Calendar.
- Asegurar validación de secret, reintentos y marca anti-loop para sincronización bidireccional.
- Implementar reintentos en la app (WorkManager) y usar nodos de manejo de errores en n8n.

Ejemplos concretos de payload, endpoints y recomendaciones ya están incluidos arriba para integrar rápidamente.
<img width="1203" height="1373" alt="image" src="https://github.com/user-attachments/assets/ddb39434-f2c6-42c0-8383-533985577467" />

<img width="1197" height="1354" alt="image" src="https://github.com/user-attachments/assets/dd0b2bcd-4f74-485b-9adf-2a7f949e8308" />
<img width="1096" height="581" alt="image" src="https://github.com/user-attachments/assets/36b59e1b-db17-41fa-ba71-5695aa180e30" />

VIDEO PEQUEÑA MUESTRA DE LA FUCNIONALIDAD

https://drive.google.com/file/d/1dd-unqNb2ePmo29kga_LTne3mp-UlY_3/view?usp=sharing


