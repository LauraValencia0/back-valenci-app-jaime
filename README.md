Documentación de endpoints Valenci App - 31 de enero de 2026.


1. Autenticación (controlador-autenticacion)
Gestiona el acceso al sistema.
POST /api/auth/login
Descripción: Inicia sesión para obtener un token de acceso.
Cuerpo de la petición (JSON): correo, contrasena.
Respuesta Exitosa (200 OK): Retorna un objeto con el token de tipo string.

2. Gestión de Perfil (controlador-cuenta)
Permite a los usuarios autenticados gestionar su propia información.
GET /api/cuenta/perfil
Descripción: Obtiene los datos del perfil del usuario actual (ID, nombre, correo, rol, dirección, empresa) .
PUT /api/cuenta/perfil
Descripción: Actualiza datos básicos como nombre, dirección de envío y teléfono .
PUT /api/cuenta/cambiar-contrasena
Descripción: Permite cambiar la clave proporcionando la contrasenaActual y la nuevaContrasena.
GET /api/cuenta/historial
Descripción: Lista el historial de pedidos del usuario, incluyendo detalles de factura y productos .

3. Productos (controlador-producto)
Administración del catálogo de productos.
GET /api/productos: Lista todos los productos disponibles.
POST /api/productos : Crea un nuevo producto (requiere nombre, descripción, precio, cantidad e idProveedor) .
GET /api/productos/{id}: Obtiene el detalle de un producto específico por su ID.
PUT /api/productos/{id}: Actualiza la información de un producto existente.
DELETE /api/productos/{id}: Elimina un producto del sistema.

4. Pedidos (controlador-pedido)
Proceso de compra y seguimiento.
POST /api/pedidos
Descripción: Crea un nuevo pedido.
Cuerpo: Lista de detalles (idProducto, cantidad) y metodoPago .
GET /api/pedidos
Filtros opcionales (Query): estado (PENDIENTE, PAGADO, etc.), fecha, idProducto .
PATCH /api/pedidos/{id}/estado
Descripción: Actualiza solo el estado de un pedido (ej: de PENDIENTE a ENVIADO).

5. Facturas (controlador-factura)
Consulta de documentos contables.
GET /api/facturas: Lista todas las facturas generadas.
GET /api/facturas/{id}: Obtiene una factura por su ID.
GET /api/facturas/cliente/{idCliente}: Filtra todas las facturas pertenecientes a un cliente específico.

6. Proveedores y Clientes
Administración de los dos tipos de usuarios principales.
Entidad
GET (All)
GET (ID)
POST
PUT
DELETE
Proveedores
/api/proveedores
.../{id}
/api/proveedores
.../{id}
.../{id}
Clientes
/api/clientes
.../{id}
/api/clientes/registro
.../{id}
.../{id}


7. Administración (controlador-admin)
GET /api/admin/usuarios
Descripción: Endpoint de uso administrativo para listar todos los usuarios registrados en el sistema, mostrando su rol y empresa asociada .

Notas Técnicas Generales
Base URL: http://localhost:8080.
Formato de datos: Todos los endpoints consumen y retornan application/json.
Tipos de datos: Los IDs son generalmente integer ($int32).
¿Te gustaría que te ayude a generar el código de los DTOs en Java o la estructura de alguna Entity específica basada en estos esquemas?

