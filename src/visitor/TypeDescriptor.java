package visitor;

public class TypeDescriptor {
    private TipoTD tipo;
    private String msg;

    public TypeDescriptor(TipoTD tipo) {
        this.tipo = tipo;
        this.msg = "";
    }

    public TypeDescriptor(TipoTD tipo, String msg) {
        this.tipo = tipo;
        this.msg = msg;
    }

    public TipoTD getTipo() { 
        return tipo; 
    }
    
    public String getMsg() { 
        return msg; 
    }

    public boolean compatibile(TypeDescriptor tD) {
        if (this.tipo == TipoTD.ERROR || tD.tipo == TipoTD.ERROR) {
            return false;
        }
        if (this.tipo == tD.tipo) {
            return true;
        }
        if (this.tipo == TipoTD.FLOAT && tD.tipo == TipoTD.INT) {
            return true;
        }
        return false;
    }
}