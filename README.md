# Sistema de Inventario ElectroStore

Este documento define las especificaciones tecnicas, visuales y de organizacion para la construccion de la interfaz de usuario (UI) de la aplicacion.

---

## 1. Filosofia de Diseño (Corporate Modern UI)
El diseño debe ser minimalista, profesional y enfocada en la visualizacion y gestion optima de datos.

*   **Tipografia:** Inter
*   **Color Principal:** `#0F172A` (Azul oscuro profundo / Slate)
*   **Colores Secundarios:**
    *   `#64748B` (Gris azulado para texto secundario e iconos)
    *   `#787778` (Gris medio para bordes, divisiones y estados inactivos)
*   **Dimensiones de Pantalla Recomendadas (Resolucion base):**
    *   **Resolucion Recomendada:** Para un sistema de gestion de inventario profesional y rico en datos (tablas con multiples columnas, filtros y graficos), se recomienda encarecidamente una resolucion base de **1280x800** pixeles (16:10) en la vista principal **[main-view.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/main-view.fxml)**. Esto evita la compresion de la informacion y el uso excesivo de scroll horizontal.
    *   **Distribucion Recomendada del Espacio:**
        *   Barra Lateral (`sidebar.fxml`): **240** pixeles de ancho.
        *   Area Util de Modulos (`views/`): **1040x800** pixeles (espacio amplio y fluido al lado del sidebar para visualizacion comoda de tablas y formularios).

### Nombres Claros de Variables (fx:id)
Todos los controles interactivos deben contar con identificadores (`fx:id`) claros, descriptivos y en formato camelCase utilizando prefijos descriptivos segun el control:
*   Campos de texto: `txtUsuario`, `txtClave`
*   Botones: `btnIngresar`, `btnGuardar`
*   Tablas y Columnas: `tblProductos`, `colPrecio`
*   Etiquetas y Contenedores: `lblTitulo`, `paneNavegacion`

---

## 2. Organizacion del Proyecto FXML
Las interfaces se encuentran separadas modularmente para facilitar la reutilizacion y el mantenimiento del codigo.

### Vista Raiz
*   **[main-view.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/main-view.fxml)**: Es la **base y marco de toda la aplicacion**. Al ser la ventana contenedora principal, **aqui es donde se especifica el tamaño de pantalla recomendado del proyecto (`prefWidth="1280" prefHeight="800"`)**. Su funcion es albergar el menu lateral (`sidebar.fxml`, ancho 240px) y servir como contenedor donde se intercambiaran dinamicamente las distintas pantallas secundarias del sistema (area util de 1040px de ancho). Las demas vistas se acoplan a este tamaño base.

### Carpeta `components`
Contiene componentes graficos aislados y reutilizables en multiples pantallas del sistema.
*   **`card.fxml`**: Contenedor modular para mostrar indicadores clave, resumenes de datos o metricas rapidas.
*   **`confirm-dialog.fxml`**: Ventana emergente (modal) estandarizada para solicitar confirmaciones del usuario.
*   **`sidebar.fxml`**: Panel de navegacion lateral izquierdo para el desplazamiento entre modulos.

*Razon de su ubicacion:* Estar separados permite cargarlos e incrustarlos de forma dinamica dentro de las vistas principales sin necesidad de duplicar codigo visual.

### Carpeta `views`
Contiene las pantallas principales del flujo de la aplicacion.
*   **`login.fxml`**: Interfaz de autenticacion de usuario.
*   **`dashboard.fxml`**: Tablero principal de visualizacion del estado del negocio.

#### Modulos con Subcarpetas
Determinados modulos de negocio requieren dos tipos de interacciones: un listado general de registros y un formulario de creacion/edicion. Por ello, se agrupan en subcarpetas dedicadas:
*   **`categorias`**: `categorias.fxml` (lista) y `categoria-form.fxml` (formulario).
*   **`clientes`**: `clientes.fxml` (lista) y `cliente-form.fxml` (formulario).
*   **`compras`**: `compras.fxml` (lista) y `compra-form.fxml` (formulario).
*   **`empleados`**: `empleados.fxml` (lista) y `empleado-form.fxml` (formulario).
*   **`inventario`**: `inventario.fxml` (lista) y `inventario-form.fxml` (formulario).
*   **`productos`**: `productos.fxml` (lista) y `producto-form.fxml` (formulario).
*   **`proveedores`**: `proveedores.fxml` (lista) y `proveedor-form.fxml` (formulario).
*   **`ventas`**: `ventas.fxml` (lista) y `ventas-form.fxml` (formulario).

---

## 3. Uso de Scene Builder
Para agilizar el diseño visual de las interfaces de manera interactiva, se utilizara la herramienta **Gluon Scene Builder**.

*Nota importante:* En esta etapa, el trabajo se concentra **exclusivamente en el diseño visual de las interfaces**. No es necesario preocuparse por la logica ni realizar implementaciones de codigo Java por el momento.

### Tutorial de uso e integracion con FXML:
1.  **Instalacion:** Descarga e instala la ultima version de Scene Builder desde la web oficial de Gluon.
2.  **Abrir un archivo en la herramienta:**
    *   Ejecuta Scene Builder.
    *   Dirigete al menu superior: `File` -> `Open`.
    *   Navega en tu explorador de archivos hacia la ruta del proyecto: `src/main/resources/com/store/inventario/`.
    *   Selecciona cualquiera de las carpetas o archivos `.fxml` que desees diseñar (por ejemplo, `views/login.fxml`).
3.  **Configuracion del Controlador:**
    *   Dado que estamos en fase de diseño puramente visual, **no debes vincular ningun controlador Java todavia**.
    *   Si el archivo FXML ya cuenta con un valor asignado en `fx:controller` o en el panel `Document` -> `Controller` -> `Controller class`, puedes dejarlo como esta o en blanco. Evita crear o modificar archivos de la carpeta `controller` por ahora.
4.  **Asignacion de Identificadores (fx:id):**
    *   Para cada componente interactivo que agregues, define su identificador descriptivo en el panel derecho de Scene Builder, bajo la seccion `Code` -> `fx:id`.
5.  **Guardar el progreso:**
    *   Al guardar los cambios desde Scene Builder (`File` -> `Save` o `Ctrl + S`), las modificaciones se escribirán directamente sobre el archivo `.fxml` del proyecto.

---

## 4. Distribucion de Trabajo y Ramas de Git

Para organizar el desarrollo de manera colaborativa, se define la siguiente reparticion de tareas y ramas de trabajo en Git.

### Persona 1 (Base del Sistema y Vistas Globales)
*   **Rama:** `feature/ui-base`
*   **Tareas:**
    *   Componente: [sidebar.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/components/sidebar.fxml) (Navegacion lateral).
    *   Componente: [card.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/components/card.fxml) (Tarjetas metricas).
    *   Componente: [confirm-dialog.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/components/confirm-dialog.fxml) (Mensajes emergentes).
    *   Contenedor: [main-view.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/main-view.fxml) (Estructura principal).
    *   Vista: [login.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/login.fxml) (Acceso).
    *   Vista: [dashboard.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/dashboard.fxml) (Panel general).

### Persona 2 (Catalogo y Operaciones de Venta)
*   **Rama:** `feature/ui-catalogo-ventas`
*   **Tareas:**
    *   Modulo `productos`: [productos.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/productos/productos.fxml) y [producto-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/productos/producto-form.fxml).
    *   Modulo `categorias`: [categorias.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/categorias/categorias.fxml) y [categoria-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/categorias/categoria-form.fxml).
    *   Modulo `inventario`: [inventario.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/inventario/inventario.fxml) y [inventario-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/inventario/inventario-form.fxml).
    *   Modulo `ventas`: [ventas.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/ventas/ventas.fxml) y [ventas-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/ventas/ventas-form.fxml).

### Persona 3 (Socios Comerciales y Abastecimiento)
*   **Rama:** `feature/ui-administracion-compras`
*   **Tareas:**
    *   Modulo `proveedores`: [proveedores.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/proveedores/proveedores.fxml) y [proveedor-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/proveedores/proveedor-form.fxml).
    *   Modulo `clientes`: [clientes.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/clientes/clientes.fxml) y [cliente-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/clientes/cliente-form.fxml).
    *   Modulo `compras`: [compras.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/compras/compras.fxml) y [compra-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/compras/compra-form.fxml).
    *   Modulo `empleados`: [empleados.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/empleados/empleados.fxml) y [empleado-form.fxml](file:///c:/Users/Usuario/Desktop/inventario/src/main/resources/com/store/inventario/views/empleados/empleado-form.fxml).

---

## 5. Tutorial de Git: Trabajo en Ramas

Para evitar conflictos y trabajar de forma segura, cada persona debe trabajar en su respectiva rama.

### Paso 1: Asegurarse de estar en la rama principal y actualizada
Antes de crear una rama, asegurate de tener los ultimos cambios:
```bash
git checkout main
git pull
```

### Paso 2: Crear y cambiarse a la nueva rama
Para crear y cambiarte de inmediato a tu rama de trabajo (reemplaza `tu-rama` con el nombre asignado en el punto anterior, por ejemplo, `feature/ui-base`):
```bash
git checkout -b tu-rama
```
*Explicacion:* El comando `-b` le indica a Git que cree una nueva rama local y se mueva a ella inmediatamente.

### Paso 3: Confirmar en que rama estas trabajando
Para verificar que te encuentras en tu rama correspondiente:
```bash
git branch
```
*Resultado:* Veras una lista de ramas locales, y la rama activa tendra un asterisco (*) al lado.

### Paso 4: Confirmar y subir los cambios (Buenas practicas de Commit)
Al confirmar tus cambios, utiliza **Conventional Commits** para mantener un historial limpio y profesional. Los prefijos recomendados para esta fase visual son:
*   `feat: ` Para añadir una nueva vista o componente visual (ej. `feat: diseno de la pantalla de login`).
*   `fix: ` Para corregir un error visual o de layout (ej. `fix: alineacion de botones en sidebar`).
*   `chore: ` Para tareas de mantenimiento, cambios de organizacion o documentacion (ej. `chore: actualizacion de guia de interfaces`).

Ejemplo de flujo para confirmar y subir cambios por primera vez:
```bash
git add .
git commit -m "feat: diseno de la pantalla de login y componentes base"
git push -u origin tu-rama
```
*Explicacion:* `-u origin tu-rama` asocia tu rama local con el repositorio remoto la primera vez. En las siguientes ocasiones bastara con escribir `git push`.
