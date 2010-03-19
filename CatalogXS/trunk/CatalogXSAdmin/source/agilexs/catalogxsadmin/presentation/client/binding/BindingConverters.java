package agilexs.catalogxsadmin.presentation.client.binding;

public class BindingConverters {

  public static final BindingConverter<Integer, String> INTEGER_CONVERTER = new BindingConverter<Integer, String>() {
    @Override
    public Integer convertFrom(String data) {
      return Integer.valueOf(data.trim());
    }

    @Override
    public String convertTo(Integer data) {
      return data != null ? data.toString().trim() : "";
    }
  };

  public static final BindingConverter<Double, String> DOUBLE_CONVERTER = new BindingConverter<Double, String>() {
    @Override
    public Double convertFrom(String data) {
      return Double.valueOf(data.trim());
    }

    @Override
    public String convertTo(Double data) {
      return data != null ? data.toString().trim() : "";
    }
  };

  public static final BindingConverter<Long, String> LONG_CONVERTER = new BindingConverter<Long, String>() {
    @Override
    public Long convertFrom(String data) {
      return Long.valueOf(data.trim());
    }
    
    @Override
    public String convertTo(Long data) {
      return data != null ? data.toString().trim() : "";
    }
  };
}
