export const ACTIONS = {
  TOGGLE_MODE: 'TOGGLE_MODE',
  UPDATE_FIELD: 'UPDATE_FIELD',
  SET_LOADING: 'SET_LOADING',
  SET_MESSAGE: 'SET_MESSAGE',
  CLEAR_MESSAGE: 'CLEAR_MESSAGE',
  RESET_FORM: 'RESET_FORM'
};

export const initialState = {
  isLogin: true,
  formData: {
    name: '',
    email: '',
    emailOrUsername: '',
    password: '',
    confirmPassword: ''
  },
  loading: false,
  message: {
    type: null, 
    text: ''
  }
};

export const authReducer = (state, action) => {
  switch (action.type) {
    case ACTIONS.TOGGLE_MODE:
      return {
        ...state,
        isLogin: !state.isLogin,
        formData: {
          name: '',
          email: '',
          emailOrUsername: '',
          password: '',
          confirmPassword: ''
        },
        message: { type: null, text: '' }
      };

    case ACTIONS.UPDATE_FIELD:
      return {
        ...state,
        formData: {
          ...state.formData,
          [action.payload.field]: action.payload.value
        }
      };

    case ACTIONS.SET_LOADING:
      return {
        ...state,
        loading: action.payload
      };

    case ACTIONS.SET_MESSAGE:
      return {
        ...state,
        message: {
          type: action.payload.type,
          text: action.payload.text
        }
      };

    case ACTIONS.CLEAR_MESSAGE:
      return {
        ...state,
        message: { type: null, text: '' }
      };

    case ACTIONS.RESET_FORM:
      return {
        ...state,
        formData: {
          name: '',
          email: '',
          emailOrUsername: '',
          password: '',
          confirmPassword: ''
        }
      };

    default:
      return state;
  }
};