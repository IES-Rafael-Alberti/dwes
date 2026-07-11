// Configuración de seguridad en MongoDB

use('admin');

print('== Inicio seguridad ==');

const authInfo = db.runCommand({ connectionStatus: 1, showPrivileges: true }).authInfo;
const roles = (authInfo && authInfo.authenticatedUserRoles) || [];
const isAdmin = roles.some(r => ['root', 'userAdminAnyDatabase', 'userAdmin'].includes(r.role));

if (!isAdmin) {
  print('Error: este script debe ejecutarse con un usuario administrador.');
  quit(1);
}

print('Usuario administrador detectado.');

const appDb = db.getSiblingDB('app_security_demo');
const analyticsDb = db.getSiblingDB('analytics_demo');

print('Creando usuario admin de ejemplo si no existe...');
db.getSiblingDB('admin').createUser({
  user: 'demo_admin',
  pwd: 'demo_admin_pwd',
  roles: [{ role: 'root', db: 'admin' }]
});

print('Creando app_user con readWrite sobre app_security_demo...');
appDb.createUser({
  user: 'app_user',
  pwd: 'app_user_pwd',
  roles: [{ role: 'readWrite', db: 'app_security_demo' }]
});

print('Creando analytics_user con solo lectura...');
analyticsDb.createUser({
  user: 'analytics_user',
  pwd: 'analytics_user_pwd',
  roles: [{ role: 'read', db: 'analytics_demo' }]
});

print('Probando permisos con analytics_user (insert debe fallar)...');
const analyticsConn = new Mongo();
const analyticsAuth = analyticsConn.getDB('admin').auth('analytics_user', 'analytics_user_pwd');
printjson({ analyticsAuth });
try {
  printjson(analyticsConn.getDB('analytics_demo').sample.insertOne({ value: 1 }));
} catch (e) {
  print('Error esperado: ' + e.message);
}

print('Probando permisos con app_user (dropDatabase debe fallar)...');
const appConn = new Mongo();
const appAuth = appConn.getDB('admin').auth('app_user', 'app_user_pwd');
printjson({ appAuth });
try {
  printjson(appConn.getDB('app_security_demo').dropDatabase());
} catch (e) {
  print('Error esperado: ' + e.message);
}

print('== Fin seguridad ==');
