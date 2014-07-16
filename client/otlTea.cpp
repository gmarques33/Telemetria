// Iluminacao.c - Isabel H. Manssour
// Um programa OpenGL que exemplifica a visualização 
// de objetos 3D com a inserção de uma fonte de luz.
// Este código está baseado nos exemplos disponíveis no livro 
// "OpenGL SuperBible", 2nd Edition, de Richard S. e Wright Jr.

#ifdef __linux__
    #include <GL/glut.h>
#else
    #include <glut/glut.h>
#endif
#include <iostream>
#include <cstdlib>
#include "otlclient.h"

using namespace std;

GLfloat angle, fAspect;
otlclient otl;
string sensor;
            
// Função callback chamada para fazer o desenho
void Desenha(void)
{

	// Limpa a janela e o depth buffer
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glColor3f(0.0f, 0.0f, 1.0f);

	// Desenha o teapot com a cor corrente (solid)
	glutSolidTeapot(50.0f);

	glutSwapBuffers();
}

// Inicializa parâmetros de rendering
void Inicializa (void)
{ 
	GLfloat luzAmbiente[4]={0.2,0.2,0.2,1.0}; 
	GLfloat luzDifusa[4]={0.7,0.7,0.7,1.0};	   // "cor" 
	GLfloat luzEspecular[4]={1.0, 1.0, 1.0, 1.0};// "brilho" 
	GLfloat posicaoLuz[4]={200.0, 0.0, 0.0, 0.0};

	// Capacidade de brilho do material
	GLfloat especularidade[4]={1.0,1.0,1.0,1.0}; 
	GLint especMaterial = 60;

 	// Especifica que a cor de fundo da janela será preta
	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	
	// Habilita o modelo de colorização de Gouraud
	glShadeModel(GL_SMOOTH);

	// Define a refletância do material 
	glMaterialfv(GL_FRONT,GL_SPECULAR, especularidade);
	// Define a concentração do brilho
	glMateriali(GL_FRONT,GL_SHININESS,especMaterial);

	// Ativa o uso da luz ambiente 
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT, luzAmbiente);
glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);

	// Define os parâmetros da luz de número 0
	//glLightfv(GL_LIGHT0, GL_AMBIENT, luzAmbiente); 
	glLightfv(GL_LIGHT0, GL_DIFFUSE, luzDifusa );
	glLightfv(GL_LIGHT0, GL_SPECULAR, luzEspecular );
	glLightfv(GL_LIGHT0, GL_POSITION, posicaoLuz );

	// Habilita a definição da cor do material a partir da cor corrente
	glEnable(GL_COLOR_MATERIAL);
	//Habilita o uso de iluminação
	glEnable(GL_LIGHTING);  
	// Habilita a luz de número 0
	glEnable(GL_LIGHT0);
	// Habilita o depth-buffering
	glEnable(GL_DEPTH_TEST);

	angle=45;
}

// Função usada para especificar o volume de visualização
void EspecificaParametrosVisualizacao(float x, float y, float z)
{
	// Especifica sistema de coordenadas de projeção
	glMatrixMode(GL_PROJECTION);
	// Inicializa sistema de coordenadas de projeção
	glLoadIdentity();

	// Especifica a projeção perspectivaglMaterialfv
	gluPerspective(angle,fAspect,0.4,500);

	// Especifica sistema de coordenadas do modelo
	glMatrixMode(GL_MODELVIEW);
	// Inicializa sistema de coordenadas do modelo
	glLoadIdentity();

	// Especifica posição do observador e do alvo
	gluLookAt(z,x,y, 0,0,0, 0,1,0);
GLfloat posicaoLuz[4]={200.0, 0.0, 0.0, 0.0};
	glLightfv(GL_LIGHT0, GL_POSITION, posicaoLuz );
}

// Função callback chamada quando o tamanho da janela é alterado 
void AlteraTamanhoJanela(GLsizei w, GLsizei h)
{
	// Para previnir uma divisão por zero
	if ( h == 0 ) h = 1;

	// Especifica o tamanho da viewport
	glViewport(0, 0, w, h);
 
	// Calcula a correção de aspecto
	fAspect = (GLfloat)w/(GLfloat)h;

	EspecificaParametrosVisualizacao(0.0, 0.0, 0.0);
}

// Função callback chamada para gerenciar eventos do mouse
void GerenciaMouse(int button, int state, int j, int k) //Era int x, int y
{
    string x;
    string y;
    string z;
    
    ///////////////////////////////////////////////////////////////
    //		Le os dados um determinado sensor		//
    //////////////////////////////////////////////////////////////
    //while(1){
      cout << "\nGet " << sensor << ": " << otl.get(sensor, 0) << endl;
      cout << "Timestamp: " << otl.getLastTimestamp() << endl;
      cout << "Message: " << otl.getLastMsg() << endl;
      cout << "Info: " << otl.getLastInfo() << endl;
      x = y = z = "";
      int i = 0;
	while(otl.getLastInfo()[i] != ' ' && otl.getLastInfo().length() > i)
	  z += otl.getLastInfo()[i++];
	i++;
	while(otl.getLastInfo()[i] != ' ' && otl.getLastInfo().length() > i)
	  y += otl.getLastInfo()[i++];
	i++;
	while(otl.getLastInfo()[i] != ' ' && otl.getLastInfo().length() > i)
	  x += otl.getLastInfo()[i++];
	
	EspecificaParametrosVisualizacao(atof(x.c_str())*20, atof(y.c_str())*20, atof(z.c_str())*20);
	glutPostRedisplay();
    //}
}

// Função callback chamada pela GLUT a cada intervalo de tempo
// (a window não está sendo redimensionada ou movida)
void Timer(int value)
{
    string x;
    string y;
    string z;
    
    ///////////////////////////////////////////////////////////////
    //		Le os dados um determinado sensor		//
    //////////////////////////////////////////////////////////////
      cout << "\nGet " << sensor << ": " << otl.get(sensor, 0) << endl;
      cout << "Timestamp: " << otl.getLastTimestamp() << endl;
      cout << "Message: " << otl.getLastMsg() << endl;
      cout << "Info: " << otl.getLastInfo() << endl;
      x = y = z = "";
      int i = 0;
	while(otl.getLastInfo()[i] != ' ' && otl.getLastInfo().length() > i)
	  z += otl.getLastInfo()[i++];
	i++;
	while(otl.getLastInfo()[i] != ' ' && otl.getLastInfo().length() > i)
	  y += otl.getLastInfo()[i++];
	i++;
	while(otl.getLastInfo()[i] != ' ' && otl.getLastInfo().length() > i)
	  x += otl.getLastInfo()[i++];
	
	EspecificaParametrosVisualizacao(atof(x.c_str())*20, atof(y.c_str())*20, atof(z.c_str())*20);
	glutPostRedisplay();
	glutTimerFunc(15, Timer, 1);
}


// Programa Principal
int main(int argc, char *argv[])
{    

    sensor = "Accelerometer";
    
    ///////////////////////////////////////////////////////////////
    //			Conecta ao dispositivo 			//
    ////////////////////////////////////////////////////////////// 
    
    std::cout << "Conectando....." << std::endl;
    std::cout << otl.connect(argv[1], atoi(argv[2])) << std::endl;
    std::cout << "Timestamp: " << otl.getLastTimestamp() << std::endl;
    std::cout << "Message: " << otl.getLastMsg() << std::endl;
    std::cout << "Info: " << otl.getLastInfo() << std::endl;
    
     ///////////////////////////////////////////////////////////////
    //		Recupera a lista de sensores diponiveis		//
    //////////////////////////////////////////////////////////////
    
    std::cout << "\nGet SensorList: " << otl.updateSensorList() << std::endl;
    std::cout << "Timestamp: " << otl.getLastTimestamp() << std::endl;
    std::cout << "Message: " << otl.getLastMsg() << std::endl;
    std::cout << "Info: " << otl.getLastInfo() << std::endl;
    
    ///////////////////////////////////////////////////////////////
    //			Liga um determinado sensor		//
    //////////////////////////////////////////////////////////////

    std::cout << "\nTurn " << sensor << " on: " << otl.turnOn(sensor, 0) << std::endl;
    std::cout << "Timestamp: " << otl.getLastTimestamp() << std::endl;
    std::cout << "Message: " << otl.getLastMsg() << std::endl;
    std::cout << "Info: " << otl.getLastInfo() << std::endl;
    
        glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(400,350);
	glutCreateWindow("Visualizacao 3D");
	glutDisplayFunc(Desenha);
	glutReshapeFunc(AlteraTamanhoJanela);
	glutMouseFunc(GerenciaMouse);
	glutTimerFunc(15, Timer, 1);
	Inicializa();
 	glutMainLoop();
}

