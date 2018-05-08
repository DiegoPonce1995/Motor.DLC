package bd;

import datos.Termino;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class ArchivoToHM {

    private File file[];

    public ArchivoToHM(File file[]) {
        this.file = file;
    }

    public String removeAcentos(String str) {
        //Reemplaza las vocales con acentos y dieresis por las vocales sin estos.
        String acentos = "áÁäÄéÉëËíÍïÏóÓöÖúÚüÜ";
        String correxion = "aAaAeEeEiIiIoOoOuUuU";
        String corregido = str;
        for (int i = 0; i < acentos.length(); i++) {
            corregido = corregido.replace(acentos.charAt(i), correxion.charAt(i));
        }
        return corregido;
    }

    public Map[] fileToHM() {   //Genera un mapa con todas las palabras de un archivo seleccionado.
        Map terminoHM = new LinkedHashMap();
        Map posteoHM = new LinkedHashMap();
        
        String titulo="";
        int contador=0;
        int auxcontador=0;
        try {
            for (Object o : this.file) {
//                
                contador++;
                auxcontador++;
                File fa = (File) o; //Lectura del archivo
                FileReader fr = new FileReader(fa);
                BufferedReader br = new BufferedReader(fr);
                //Inicializacion
                String s = br.readLine();
                                
                if (contador<3){ //Para agregarle el titulo a el documento (las dos primeras lineas del libro)
                    titulo+=s;
                }
                
                StringTokenizer tokenizer;
                char comilla = '"';

                while (s != null) {
                    s = removeAcentos(s);
                    s = s.toUpperCase();
                    
                    tokenizer = new StringTokenizer(s, comilla + " $/:,.*-#[]ºª@[0123456789]()!¡_?¿;=^÷{}`´&|%°<>~©ª¬'±+");

                    while (tokenizer.hasMoreTokens()) {
                        //Guardar las palabras para procesarlas.
                        String palabra = tokenizer.nextToken();
                        
                        if (!terminoHM.containsKey(palabra)) //Primera vez que se encuentra la palabra.
                        {
                                                        
                            Termino termino = new Termino(palabra, 1, 1);
                            terminoHM.put(palabra, termino);
                            FilaPosteo fp = new FilaPosteo(palabra, fa.getName(), 1,titulo);
                            posteoHM.put(palabra + fa.getName(), fp);
//                            
                            
                        } else //Se encuentra una palabra ya existente en el vocabulario.
                        {
                            
                                //============================NUEVA IMPLEMENTACION=================================
                                if (posteoHM.containsKey(palabra + fa.getName())) { //Documento esta en el hash de posteo
                                    
                                    FilaPosteo aux = (FilaPosteo) posteoHM.remove(palabra + fa.getName());//Saca el documento y le aumenta la frecuencia para ese termino
                                    aux.aumentarFrecuencia();
                                    posteoHM.put(palabra + fa.getName(), aux);
                                    
                                    Termino aux1= (Termino) terminoHM.remove(palabra);
                                    
                                    if (aux1.getFrecuenciaMax()<aux.getFrecuencia()) { //Chequear si hace falta actualizar la frecuencia maxima del termino
                                        
                                        aux1.setFrecuenciaMax(aux.getFrecuencia());
                                        
                                    }
                                    terminoHM.put(palabra,aux1);
                                    
                                } else {    //Documento no esta en el hash de posteo
                                    
                                    FilaPosteo fp = new FilaPosteo(palabra, fa.getName(), 1,titulo);
                                    posteoHM.put(palabra + fa.getName(), fp);
//                                  
                                    
                                    Termino termAux=(Termino) terminoHM.remove(palabra);//Aumentar la cantidad de documentos del termino
                                    termAux.setCantDocumentos(termAux.getCantDocumentos()+1);
                                    terminoHM.put(palabra, termAux);
                                }
                            
                        }
                    }
                    s = br.readLine();
                }
                br.close();
                fr.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(ex.getLocalizedMessage());
        } finally {
            
            Map resp[] = new LinkedHashMap[2];
            resp[0]=terminoHM;
            resp[1]=posteoHM;
            terminoHM=null;
            posteoHM=null;
            System.out.println("Archivos leidos: "+auxcontador);
            return resp;
        }
    }
}
