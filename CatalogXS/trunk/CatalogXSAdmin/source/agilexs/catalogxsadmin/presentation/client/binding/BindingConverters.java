package agilexs.catalogxsadmin.presentation.client.binding;

public class BindingConverters {

  public static final BindingConverter<Integer, String> INTEGER_CONVERTER = new BindingConverter<Integer, String>() {
    @Override
    public Integer convertFrom(String data) {
      return Integer.valueOf(data.trim());
    }

    @Override
    public String convertTo(Integer data) {
      final String stringData = data != null ? data.toString().trim() : "";
      return stringData.isEmpty() ? "\u00a0" : stringData;
    }
  };

  public static final BindingConverter<Double, String> DOUBLE_CONVERTER = new BindingConverter<Double, String>() {
    @Override
    public Double convertFrom(String data) {
      return Double.valueOf(data.trim());
    }

    @Override
    public String convertTo(Double data) {
      final String stringData = data != null ? data.toString().trim() : "";
      return stringData.isEmpty() ? "\u00a0" : stringData;
    }
  };
}
